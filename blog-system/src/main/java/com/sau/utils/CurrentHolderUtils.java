package com.sau.utils;

import com.sau.security.AuthUser;

/**
 * 当前登录用户持有工具类。
 */
public final class CurrentHolderUtils {

    private static final ThreadLocal<AuthUser> CURRENT_LOCAL = new ThreadLocal<>();

    private CurrentHolderUtils() {
    }

    public static void setCurrentUser(AuthUser authUser) {
        CURRENT_LOCAL.set(authUser);
    }

    public static void setCurrentId(Integer id) {
        CURRENT_LOCAL.set(AuthUser.of(id));
    }

    public static AuthUser getCurrentUser() {
        AuthUser authUser = CURRENT_LOCAL.get();
        return authUser != null ? authUser : SecurityUtils.getCurrentUser();
    }

    public static Integer getCurrentId() {
        AuthUser authUser = getCurrentUser();
        return authUser != null ? authUser.getUserId() : null;
    }

    public static boolean isAdmin() {
        AuthUser authUser = getCurrentUser();
        return authUser != null && authUser.isAdmin();
    }

    public static void remove() {
        CURRENT_LOCAL.remove();
    }
}