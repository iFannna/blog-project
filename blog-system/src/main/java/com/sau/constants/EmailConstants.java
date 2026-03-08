package com.sau.constants;

/**
 * 邮件相关常量。
 */
public final class EmailConstants {

    /** 验证码有效期，单位：秒。 */
    public static final long CODE_EXPIRE = 5 * 60;

    /** 验证码发送冷却时间，单位：秒。 */
    public static final long SEND_COOLDOWN = 60;

    /** 验证码邮件模板路径。 */
    public static final String EMAIL_CODE_TEMPLATE_PATH = "classpath:templates/email-code-template.html";

    private EmailConstants() {
    }
}