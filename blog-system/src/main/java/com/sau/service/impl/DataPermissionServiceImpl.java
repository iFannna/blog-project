package com.sau.service.impl;

import com.sau.mapper.ArticleMapper;
import com.sau.mapper.CommentMapper;
import com.sau.mapper.GuestBookMapper;
import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.GuestBook;
import com.sau.service.DataPermissionService;
import com.sau.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DataPermissionServiceImpl implements DataPermissionService {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final GuestBookMapper guestBookMapper;

    @Override
    public boolean isAdmin() {
        return SecurityUtils.isAdmin();
    }

    @Override
    public void assertAdminOrSelfUser(Integer targetUserId) {
        if (isAdmin()) {
            return;
        }

        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        if (!Objects.equals(currentUserId, targetUserId)) {
            throw new AccessDeniedException("无权操作其他用户数据");
        }
    }

    @Override
    public void assertAdminOrArticleOwner(List<Integer> articleIds) {
        if (isAdmin() || CollectionUtils.isEmpty(articleIds)) {
            return;
        }

        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        List<Article> ownershipRecords = articleMapper.selectOwnershipByIds(articleIds);
        boolean hasForeignArticle = ownershipRecords.stream()
                .anyMatch(article -> !Objects.equals(currentUserId, article.getAuthorId()));
        if (hasForeignArticle) {
            throw new AccessDeniedException("无权操作其他用户的文章");
        }
    }

    @Override
    public void assertAdminOrCommentOwner(Integer commentId) {
        if (isAdmin()) {
            return;
        }

        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        Comment comment = commentMapper.selectOwnershipById(commentId);
        if (comment != null && !Objects.equals(currentUserId, comment.getUserId())) {
            throw new AccessDeniedException("无权操作其他用户的评论");
        }
    }

    @Override
    public void assertAdminOrGuestBookOwner(Integer guestBookId) {
        if (isAdmin()) {
            return;
        }

        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        GuestBook guestBook = guestBookMapper.selectOwnershipById(guestBookId);
        if (guestBook != null && !Objects.equals(currentUserId, guestBook.getUserId())) {
            throw new AccessDeniedException("无权删除其他用户的留言");
        }
    }
}
