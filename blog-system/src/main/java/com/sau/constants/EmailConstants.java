package com.sau.constants;

/**
 * 邮件相关常量定义
 */
public class EmailConstants {

    // 验证码有效期：5分钟
    public static final long CODE_EXPIRE = 5 * 60;
    // 验证码发送冷却时间：60秒（防刷核心）
    public static final long SEND_COOLDOWN = 60;
    // HTML模板路径
    public static final String REGISTER_CODE_TEMPLATE_PATH = "classpath:templates/register-code-template.html";

}
