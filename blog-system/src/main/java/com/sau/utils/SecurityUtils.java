package com.sau.utils;

import com.sau.constants.SecurityConstants;
import com.sau.security.AuthUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security 上下文工具类。
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AuthUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUser authUser) {
            return authUser;
        }
        return null;
    }

    public static Integer getCurrentUserId() {
        AuthUser authUser = getCurrentUser();
        return authUser != null ? authUser.getUserId() : null;
    }

    public static Integer requireCurrentUserId() {
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("未登录或无权访问");
        }
        return currentUserId;
    }

    public static boolean isAdmin() {
        AuthUser authUser = getCurrentUser();
        return authUser != null
                && (authUser.isAdmin() || authUser.getRoleCodes().contains(SecurityConstants.ROLE_ADMIN));
    }
}