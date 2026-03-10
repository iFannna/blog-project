package com.sau.constants;

/**
 * 响应相关常量
 */
public final class ResponseConstants {

    public static final String JSON_UTF8 = "application/json;charset=UTF-8";
    public static final String UNAUTHORIZED = "{\"code\":0,\"msg\":\"未登录或令牌无效\"}";
    public static final String FORBIDDEN = "{\"code\":0,\"msg\":\"无权限访问该资源\"}";

    private ResponseConstants() {
    }
}
