package com.sau.controller;

import lombok.RequiredArgsConstructor;

import cn.hutool.core.bean.BeanUtil;
import com.sau.annotation.Log;
import com.sau.pojo.DTO.LoginDTO;
import com.sau.pojo.VO.LoginVO;
import com.sau.pojo.entity.AuthResult;
import com.sau.pojo.entity.Result;
import com.sau.service.AuthService;
import com.sau.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录相关接口。
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class LoginController {
    private final AuthService authService;
    private final CookieUtils cookieUtils;

    /**
     * 登录。
     */
    @Log
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        log.info("登录请求: 账号={}, rememberMe={}", loginDTO.getAccount(), loginDTO.getRememberMe());

        AuthResult authResult = authService.login(loginDTO);
        if (authResult == null) {
            log.warn("用户名或密码错误: {}", loginDTO.getAccount());
            return Result.error("用户名或密码错误");
        }

        cookieUtils.setRefreshTokenCookie(response, authResult.getRefreshToken(), authResult.getTokenExpiration());

        LoginVO loginVO = new LoginVO();
        BeanUtil.copyProperties(authResult, loginVO);
        loginVO.setToken(authResult.getAccessToken());
        return Result.success(loginVO);
    }

    /**
     * 刷新登录令牌。
     */
    @GetMapping("/refresh-token")
    public Result refreshToken(HttpServletRequest request) {
        String refreshToken = cookieUtils.getRefreshTokenFromCookie(request);
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("Cookie 中的 refreshToken 不存在");
            return Result.error("Cookie 中的刷新令牌不存在");
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);
        if (newAccessToken == null) {
            return Result.error("获取登录令牌失败");
        }

        return Result.success(newAccessToken);
    }

    /**
     * 退出登录。
     */
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("退出登录请求");
        authService.logout(request, response);
        SecurityContextHolder.clearContext();
        return Result.success();
    }
}