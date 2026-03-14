package com.sau.service.impl;

import cn.hutool.http.HtmlUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sau.mapper.GuestBookMapper;
import com.sau.pojo.DTO.GuestBookCreateDTO;
import com.sau.pojo.DTO.GuestBookQueryDTO;
import com.sau.pojo.entity.GuestBook;
import com.sau.pojo.entity.PageResult;
import com.sau.security.AuthUser;
import com.sau.service.DataPermissionService;
import com.sau.service.GuestBookService;
import com.sau.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GuestBookServiceImpl implements GuestBookService {
    private final GuestBookMapper guestBookMapper;
    private final DataPermissionService dataPermissionService;

    @Override
    public PageResult<GuestBook> pageQueryGuestBooks(GuestBookQueryDTO guestBookQueryDTO) {
        try (Page<GuestBook> page = PageHelper.startPage(guestBookQueryDTO.getPage(), guestBookQueryDTO.getPageSize())) {
            List<GuestBook> guestBooks = guestBookMapper.selectPageGuestBooks(guestBookQueryDTO);
            AuthUser currentUser = SecurityUtils.getCurrentUser();
            Integer currentUserId = currentUser != null ? currentUser.getUserId() : null;
            boolean isAdmin = currentUser != null && currentUser.isAdmin();

            guestBooks.forEach(guestBook -> {
                guestBook.setCanDelete(currentUserId != null
                        && (isAdmin || Objects.equals(currentUserId, guestBook.getUserId())));
                if (Integer.valueOf(1).equals(guestBook.getIsAnonymous())) {
                    guestBook.setNickname("匿名访客");
                    guestBook.setAvatar(null);
                }
            });
            return new PageResult<>(page.getTotal(), guestBooks);
        }
    }

    @Override
    public List<GuestBook> queryFeaturedGuestBooks(Integer limit) {
        int finalLimit = (limit == null || limit <= 0 || limit > 16) ? 16 : limit;
        List<GuestBook> guestBooks = guestBookMapper.selectFeaturedGuestBooks(finalLimit);

        guestBooks.forEach(guestBook -> {
            if (Integer.valueOf(1).equals(guestBook.getIsAnonymous())) {
                guestBook.setNickname("匿名访客");
                guestBook.setAvatar(null);
            }
        });

        return guestBooks;
    }

    @Override
    public void createGuestBook(GuestBookCreateDTO guestBookCreateDTO) {
        String sanitizedContent = sanitizeGuestBookContent(guestBookCreateDTO.getContent());
        if (!StringUtils.hasText(sanitizedContent)) {
            throw new RuntimeException("留言内容不能为空");
        }
        if (sanitizedContent.length() > 1000) {
            throw new RuntimeException("留言内容不能超过 1000 个字符");
        }

        GuestBook guestBook = new GuestBook();
        guestBook.setUserId(SecurityUtils.requireCurrentUserId());
        guestBook.setContent(sanitizedContent);
        guestBook.setStatus(1);
        guestBook.setIsAnonymous(Boolean.TRUE.equals(guestBookCreateDTO.getAnonymous()) ? 1 : 0);
        guestBookMapper.insertGuestBook(guestBook);
    }

    @Override
    public void reportGuestBook(Integer id) {
        SecurityUtils.requireCurrentUserId();
        GuestBook guestBook = guestBookMapper.selectOwnershipById(id);
        if (guestBook == null) {
            throw new RuntimeException("留言不存在");
        }
        if (guestBookMapper.reportById(id) <= 0) {
            throw new RuntimeException("留言举报失败，请稍后重试");
        }
    }

    @Override
    public void deleteGuestBook(Integer id) {
        GuestBook guestBook = guestBookMapper.selectOwnershipById(id);
        if (guestBook == null) {
            throw new RuntimeException("留言不存在");
        }
        dataPermissionService.assertAdminOrGuestBookOwner(id);
        if (guestBookMapper.deleteById(id) <= 0) {
            throw new RuntimeException("留言删除失败，请稍后重试");
        }
    }

    private String sanitizeGuestBookContent(String content) {
        if (content == null) {
            return "";
        }

        String normalizedContent = content.replace("\r\n", "\n").replace('\r', '\n');
        String withoutDangerousBlocks = normalizedContent
                .replaceAll("(?is)<script[^>]*>.*?</script>", "")
                .replaceAll("(?is)<style[^>]*>.*?</style>", "")
                .replaceAll("(?is)<iframe[^>]*>.*?</iframe>", "")
                .replaceAll("(?is)<object[^>]*>.*?</object>", "")
                .replaceAll("(?is)<embed[^>]*>.*?</embed>", "")
                .replaceAll("(?is)<link[^>]*>", "");
        return HtmlUtil.cleanHtmlTag(withoutDangerousBlocks).trim();
    }
}
