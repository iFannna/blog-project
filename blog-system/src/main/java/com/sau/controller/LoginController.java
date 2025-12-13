package com.sau.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录相关接口
 */
@Slf4j
@RestController
public class LoginController {

    // 注入认证服务
    @Autowired
    private AuthService authService;
    // 注入Cookie工具类
    @Autowired
    private CookieUtils cookieUtils;

    /**
     * 登录
     */
    @Log
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        log.info("登录请求: 账号={}, rememberMe={}", loginDTO.getAccount(), loginDTO.getRememberMe());

        // 调用认证服务处理登录业务
        AuthResult authResult = authService.login(loginDTO);
        if (authResult == null) {
            log.warn("用户名或密码错误: {}", loginDTO.getAccount());
            return Result.error("用户名或密码错误");
        }

        // 设置refreshToken到Cookie
        log.info("设置refreshToken到Cookie: {}", authResult.getRefreshToken());
        cookieUtils.setRefreshTokenCookie(response, authResult.getRefreshToken(), authResult.getTokenExpiration());

        // 封装返回结果
        LoginVO loginVO = new LoginVO(
                authResult.getName(),
                authResult.getUsername(),
                authResult.getAvatar(),
                authResult.getAccessToken()
        );
        return Result.success(loginVO);
    }

    /**
     * 刷新登录令牌
     */
    @GetMapping("/refresh-token")
    public Result refreshToken(HttpServletRequest request) {
        // 从Cookie获取refreshToken
        String refreshToken = cookieUtils.getRefreshTokenFromCookie(request);
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("Cookie中的refreshToken不存在");
            return Result.error("Cookie中的刷新令牌不存在");
        }

        // 调用认证服务刷新token
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        if (newAccessToken == null) {
            return Result.error("获取登录令牌失败");
        }

        return Result.success(newAccessToken);
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("退出登录请求");
        // 调用认证服务处理退出逻辑
        authService.logout(request,response);
        // 清除Spring Security安全上下文
        SecurityContextHolder.clearContext();

        log.info("退出登录成功");
        return Result.success();
    }
}
