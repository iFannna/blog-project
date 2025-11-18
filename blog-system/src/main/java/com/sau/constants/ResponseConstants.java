package com.sau.constants;

/**
 * 响应相关常量定义类
 * 统一管理响应类型、状态码、固定响应信息等
 */
public final class ResponseConstants {

    /** JSON格式响应（UTF-8编码） */
    public static final String JSON_UTF8 = "application/json;charset=UTF-8";

    /** 未登录或令牌无效的响应信息 */
    public static final String UNAUTHORIZED = "{\"code\":0,\"msg\":\"未登录或令牌无效\"}";

}