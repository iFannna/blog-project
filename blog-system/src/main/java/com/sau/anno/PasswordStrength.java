package com.sau.anno;

import com.sau.utils.PasswordValidatorUtils;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 密码强度校验注解
 */
// 注解作用范围：字段
@Target({ElementType.FIELD})
// 注解保留时机：运行时
@Retention(RetentionPolicy.RUNTIME)
// 关联校验器
@Constraint(validatedBy = PasswordValidatorUtils.class)
public @interface PasswordStrength {

    // 校验失败提示信息
    String message() default "密码强度不足，需包含字母和数字";

    // 分组校验（默认）
    Class<?>[] groups() default {};

    // 附加信息（默认）
    Class<? extends Payload>[] payload() default {};
}