package com.sau.service;

import com.sau.pojo.DTO.RegisterDTO;


public interface UserService {

    /**
     * 注册
     */
    void register(RegisterDTO request);
    /**
     * 判断用户名是否存在
     */
    boolean hasUsername(String username);
    /**
     * 邮箱是否存在
     */
    boolean hasEmail(String email);
}
