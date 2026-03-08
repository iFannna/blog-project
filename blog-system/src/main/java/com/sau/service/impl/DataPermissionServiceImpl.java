package com.sau.service.impl;

import com.sau.mapper.ArticleMapper;
import com.sau.mapper.CommentMapper;
import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.Comment;
import com.sau.service.DataPermissionService;
import com.sau.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 数据级权限校验服务实现。
 */
@Service
@RequiredArgsConstructor
public class DataPermissionServiceImpl implements DataPermissionService {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;

    /**
     * 判断当前用户是否为管理员。
     */
    @Override
    public boolean isAdmin() {
        return SecurityUtils.isAdmin();
    }

    /**
     * 校验当前用户是否可以操作指定用户数据。
     */
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

    /**
     * 校验当前用户是否拥有文章归属权。
     */
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

    /**
     * 校验当前用户是否拥有评论归属权。
     */
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
}