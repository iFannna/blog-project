package com.sau.service;

import com.sau.pojo.DTO.LoginDTO;
import com.sau.pojo.entity.AuthResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    AuthResult login(LoginDTO loginDTO);

    /**
     * 刷新 accessToken
     */
    String refreshAccessToken(String refreshToken);

    /**
     * 用户退出登录
     */
    void logout(HttpServletRequest request, HttpServletResponse response);
}
