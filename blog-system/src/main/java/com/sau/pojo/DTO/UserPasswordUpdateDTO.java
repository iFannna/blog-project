package com.sau.pojo.DTO;

import com.sau.annotation.PasswordStrength;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordUpdateDTO {

    /**
     * 用户名
     */
    private String username;
    /**
     * 旧密码
     */
    private String oldPassword;
    /**
     * 密码
     * 长度必须在6-18位之间
     */
    @NotBlank
    @Size(min = 6, max = 18)
    @PasswordStrength()
    private String newPassword;
}
