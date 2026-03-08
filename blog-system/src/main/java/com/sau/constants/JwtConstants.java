package com.sau.constants;

/**
 * JWT 相关常量。
 */
public final class JwtConstants {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";

    /** accessToken 有效期：15 分钟。 */
    public static final long TEMP_TOKEN_EXPIRATION = 15 * 60 * 1000L;

    /** 短期 refreshToken 有效期：1 天。 */
    public static final long SHORT_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L;

    /** 长期 refreshToken 有效期：7 天。 */
    public static final long LONG_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    private JwtConstants() {
    }
}