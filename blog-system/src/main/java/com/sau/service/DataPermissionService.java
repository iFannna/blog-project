package com.sau.service;

import java.util.List;

/**
 * 数据级权限校验服务。
 */
public interface DataPermissionService {

    /**
     * 判断当前用户是否为管理员。
     */
    boolean isAdmin();

    /**
     * 校验当前用户是否为目标用户本人，管理员直接放行。
     */
    void assertAdminOrSelfUser(Integer targetUserId);

    /**
     * 校验当前用户是否拥有指定文章集合的归属权，管理员直接放行。
     */
    void assertAdminOrArticleOwner(List<Integer> articleIds);

    /**
     * 校验当前用户是否拥有指定评论的归属权，管理员直接放行。
     */
    void assertAdminOrCommentOwner(Integer commentId);
}