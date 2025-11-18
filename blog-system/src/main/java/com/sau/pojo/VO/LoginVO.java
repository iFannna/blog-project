package com.sau.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
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
     * token令牌
     */
    private String token;
}
