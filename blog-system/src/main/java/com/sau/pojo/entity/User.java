package com.sau.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // 用户ID
    private Integer id;
    // 昵称
    private String nickname;
    // 简介
    private String introduction;
    // 用户名
    private String username;
    // 账号
    private String account;
    // 密码
    private String password;
    // 邮箱
    private String email;
    // 手机号
    private String phone;
    // 头像
    private String avatar;
    // 账号状态
    private Integer status;
    // 用户类型
    private Integer userType;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
