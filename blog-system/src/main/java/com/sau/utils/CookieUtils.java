package com.sau.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * Cookie 工具类
 */
@Component
public class CookieUtils {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    /**
     * 从 Cookie 中获取 refreshToken
     */
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 写入 refreshToken Cookie
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, long maxAge) {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) maxAge);
        response.addCookie(refreshTokenCookie);
    }

    /**
     * 清理 refreshToken Cookie
     */
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
    }
}
