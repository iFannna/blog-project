package com.sau.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 令牌生成结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {
    /**
     * 用户id
     */
    private Integer userId;
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
}