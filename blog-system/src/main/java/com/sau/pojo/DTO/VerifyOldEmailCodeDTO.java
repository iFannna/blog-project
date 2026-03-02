package com.sau.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOldEmailCodeDTO {
    /**
     * 旧邮箱
     */
    @NotBlank
    private String oldEmail;

    /**
     * 旧邮箱验证码
     */
    @NotBlank
    private String oldEmailCode;
}
