package com.sau.constants;

/**
 * Redis Key 统一定义
 */
public final class RedisKeyConstants {

    private static final String ACCESS_TOKEN_PREFIX = "access_token:";
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "access_token_blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String EMAIL_CODE_PREFIX = "email:code:";
    private static final String EMAIL_CODE_COOLDOWN_SUFFIX = ":cooling";
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final String TEMP_EMAIL_CHANGE_PREFIX = "temp:email:change:";

    private RedisKeyConstants() {
    }

    public static String accessTokenKey(Integer userId) {
        return ACCESS_TOKEN_PREFIX + userId;
    }

    public static String accessTokenBlacklistKey(String accessToken) {
        return ACCESS_TOKEN_BLACKLIST_PREFIX + accessToken;
    }

    public static String refreshTokenKey(Integer userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }

    public static String emailCodeKey(String email) {
        return EMAIL_CODE_PREFIX + email;
    }

    public static String emailCodeCooldownKey(String email) {
        return emailCodeKey(email) + EMAIL_CODE_COOLDOWN_SUFFIX;
    }

    public static String captchaKey(String captchaParam) {
        return CAPTCHA_PREFIX + captchaParam;
    }

    public static String tempEmailChangeKey(String tempToken) {
        return TEMP_EMAIL_CHANGE_PREFIX + tempToken;
    }
}
