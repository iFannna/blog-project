package com.sau.utils;

import com.sau.annotation.PasswordStrength;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidatorUtils implements ConstraintValidator<PasswordStrength, String> {

    // 允许的字符集：指定特殊字符 + 大小写字母 + 数字
    // 注意：正则中需要对特殊字符转义（如[]），并处理-的位置避免被解析为范围
    private static final String ALLOWED_CHARS_REGEX =
            "^[@#$%&*a-zA-Z0-9]+$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false; // 非空校验由@NotBlank处理，此处兜底
        }

        // 1. 校验是否包含至少1个字母（大小写）
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        // 2. 校验是否包含至少1个数字
        boolean hasDigit = password.matches(".*\\d.*");
        // 3. 校验是否只包含允许的字符（无其他字符）
        boolean hasOnlyAllowedChars = password.matches(ALLOWED_CHARS_REGEX);

        // 三个条件必须同时满足
        return hasLetter && hasDigit && hasOnlyAllowedChars;
    }
}