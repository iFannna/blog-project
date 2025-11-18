package com.sau.constants;

/**
 * JWT相关常量定义
 */
public class JwtConstants {

    // 用户ID
    public static final String USER_ID = "userId";
    // 用户名
    public static final String USERNAME = "username";


    // AccessToken过期时间（毫秒）：15分钟
    public static final long TEMP_TOKEN_EXPIRATION = 15 * 60 * 1000L;
    // 短期RefreshToken过期时间（毫秒）：1天
    public static final long SHORT_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L;
    // 长期RefreshToken过期时间（毫秒）：7天
    public static final long LONG_TOKEN_EXPIRATION =7 * 24 * 60 * 60 * 1000L;
}