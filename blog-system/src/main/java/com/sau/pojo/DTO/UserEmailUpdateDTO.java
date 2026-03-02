package com.sau.pojo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailUpdateDTO {
    /**
     * 用户名
     */
    @NotBlank
    private String username;

    /**
     * 新邮箱
     */
    @NotBlank
    @Email(regexp = "^[^\s@]+@[^\s@]+\\.[^\s@]+$")
    private String newEmail;

    /**
     * 新邮箱验证码
     */
    @NotBlank
    private String newEmailCode;

    /**
     * 临时换绑令牌
     */
    @NotBlank
    private String tempToken;
}
