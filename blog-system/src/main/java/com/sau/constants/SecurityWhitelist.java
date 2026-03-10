package com.sau.constants;

import org.springframework.util.AntPathMatcher;

/**
 * 安全白名单定义
 * <p>
 * 统一维护匿名可访问接口，避免在过滤器与安全配置中重复维护
 */
public final class SecurityWhitelist {

    /** 匿名可访问的 POST 接口。 */
    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/login",
            "/register"
    };

    /** 匿名可访问的 GET 接口。 */
    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/refresh-token",
            "/send-register-code",
            "/article",
            "/article/*",
            "/article/hot",
            "/article/mostLike",
            "/article/mostStar",
            "/article/mostShare",
            "/tag",
            "/category",
            "/comment",
            "/comment/reply"
    };

    private SecurityWhitelist() {
    }

    /**
     * 判断当前请求是否属于白名单接口
     */
    public static boolean isPublicEndpoint(String method, String uri, AntPathMatcher antPathMatcher) {
        return matches(method, uri, "POST", PUBLIC_POST_ENDPOINTS, antPathMatcher)
                || matches(method, uri, "GET", PUBLIC_GET_ENDPOINTS, antPathMatcher);
    }

    private static boolean matches(String requestMethod,
                                   String uri,
                                   String targetMethod,
                                   String[] patterns,
                                   AntPathMatcher antPathMatcher) {
        if (!targetMethod.equalsIgnoreCase(requestMethod)) {
            return false;
        }
        for (String pattern : patterns) {
            if (antPathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }
}
