package com.sau.pojo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendNewEmailCodeDTO {
    /**
     * 新邮箱
     */
    @NotBlank
    @Email(regexp = "^[^\s@]+@[^\s@]+\\.[^\s@]+$")
    private String newEmail;

    /**
     * 临时换绑令牌
     */
    @NotBlank
    private String tempToken;
}
