package com.sau.service;

import com.sau.security.AuthUser;

/**
 * 认证用户加载服务
 */
public interface AuthUserService {

    /**
     * 根据用户 ID 从数据库加载当前用户的身份快照
     */
    AuthUser loadAuthUser(Integer userId);
}
