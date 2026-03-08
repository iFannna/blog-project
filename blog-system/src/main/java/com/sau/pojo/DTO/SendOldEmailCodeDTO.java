package com.sau.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOldEmailCodeDTO {

    /**
     * 旧邮箱
     */
    @NotBlank
    private String oldEmail;

    /**
     * 验证码参数
     */
    @NotBlank
    private String captchaParam;
}