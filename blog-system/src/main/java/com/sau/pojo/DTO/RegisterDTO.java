package com.sau.pojo.DTO;

import com.sau.annotation.PasswordStrength;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 注册请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    /**
     * 用户名
     * 只能包含英文字母和数字且长度必须在6-12位之间
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    @Size(min = 6, max = 12)
    private String username;

    /**
     * 密码
     * 长度必须在6-18位之间
     */
    @NotBlank
    @Size(min = 6, max = 18)
    @PasswordStrength()
    private String password;

    /**
     * 邮箱
     */
    @NotBlank
    @Email(regexp = "^[^\s@]+@[^\s@]+\\.[^\s@]+$")
    private String email;

    /**
     * 邮箱验证码
     */
    @NotBlank
    private String emailVerificationCode;
}