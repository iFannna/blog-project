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
     * 旧密码
     */
    @NotBlank
    private String oldPassword;

    /**
     * 新密码
     * 长度必须在 6-18 位之间
     */
    @NotBlank
    @Size(min = 6, max = 18)
    @PasswordStrength
    private String newPassword;
}
