package com.sau.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    /**
     * 账号
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 记住我
     */
    private Boolean rememberMe;

}