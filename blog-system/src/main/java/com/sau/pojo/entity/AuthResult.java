package com.sau.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 认证结果封装类
 */
@Data
@AllArgsConstructor
public class AuthResult {
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 昵称
     */
    private String name;
    /**
     * 用户名
     */
    private String username;
    /**
     * 头像
     */
    private String avatar;
    /**
     * accessToken令牌
     */
    private String accessToken;
    /**
     * refreshToken令牌
     */
    private String refreshToken;
    /**
     * 令牌过期时间
     */
    private Long tokenExpiration;

}
