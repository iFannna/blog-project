package com.sau.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码验证结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVerifyVO {
    /**
     * 验证码验证结果
     */
    private boolean success;
    /**
     * 验证码
     */
    private String verifyCode;
    /**
     * 验证码验证结果信息
     */
    private String message;
}