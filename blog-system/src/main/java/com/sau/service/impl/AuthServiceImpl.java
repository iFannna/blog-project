package com.sau.service.impl;

import lombok.RequiredArgsConstructor;

import com.sau.constants.JwtConstants;
import com.sau.constants.RequestConstants;
import com.sau.mapper.SysUserMapper;
import com.sau.pojo.DTO.LoginDTO;
import com.sau.pojo.entity.AuthResult;
import com.sau.pojo.entity.SysUser;
import com.sau.security.AuthUser;
import com.sau.service.AuthService;
import com.sau.service.AuthUserService;
import com.sau.utils.CookieUtils;
import com.sau.utils.JwtUtils;
import com.sau.utils.RedisTokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类。
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final SysUserMapper sysUserMapper;
    private final AuthUserService authUserService;
    private final RedisTokenUtils redisTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final CookieUtils cookieUtils;

    /**
     * 处理登录逻辑。
     */
    @Override
    public AuthResult login(LoginDTO loginDTO) {
        String account = loginDTO.getAccount();
        String rawPassword = loginDTO.getPassword();

        SysUser dbSysUser = sysUserMapper.selectByAccount(account);
        if (dbSysUser == null) {
            log.warn("账号不存在: {}", account);
            return null;
        }
        if (dbSysUser.getStatus() == null || dbSysUser.getStatus() != 1) {
            log.warn("账号已被禁用: {}", account);
            return null;
        }

        boolean matches = passwordEncoder.matches(rawPassword, dbSysUser.getPassword());
        if (!matches) {
            log.warn("密码错误: {}", account);
            return null;
        }

        AuthUser authUser = authUserService.loadAuthUser(dbSysUser.getId());
        if (authUser == null) {
            return null;
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstants.USER_ID, dbSysUser.getId());
        claims.put(JwtConstants.USERNAME, dbSysUser.getUsername());

        String accessToken = JwtUtils.generateToken(claims, JwtConstants.TEMP_TOKEN_EXPIRATION);
        redisTokenUtils.saveAccessToken(dbSysUser.getId(), accessToken, JwtConstants.TEMP_TOKEN_EXPIRATION / 1000);

        long tokenExpiration = Boolean.TRUE.equals(loginDTO.getRememberMe())
                ? JwtConstants.LONG_TOKEN_EXPIRATION
                : JwtConstants.SHORT_TOKEN_EXPIRATION;

        String refreshToken = JwtUtils.generateToken(claims, tokenExpiration);
        redisTokenUtils.saveRefreshToken(dbSysUser.getId(), refreshToken, tokenExpiration / 1000);

        return new AuthResult(
                dbSysUser.getId(),
                dbSysUser.getNickname(),
                dbSysUser.getUsername(),
                dbSysUser.getAvatar(),
                accessToken,
                refreshToken,
                tokenExpiration);
    }

    /**
     * 刷新 accessToken。
     */
    @Override
    public String refreshAccessToken(String refreshToken) {
        try {
            Claims claims = JwtUtils.parseToken(refreshToken);
            Integer userId = Integer.valueOf(claims.get(JwtConstants.USER_ID).toString());
            AuthUser authUser = authUserService.loadAuthUser(userId);
            if (authUser == null) {
                return null;
            }

            String storedRefreshToken = redisTokenUtils.getRefreshToken(userId);
            if (!refreshToken.equals(storedRefreshToken)) {
                log.warn("refreshToken 不匹配，可能已失效或被替换");
                return null;
            }
            if (redisTokenUtils.getAccessToken(userId) != null) {
                log.warn("用户 accessToken 尚未失效，无需重复刷新");
                return null;
            }

            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put(JwtConstants.USER_ID, userId);
            newClaims.put(JwtConstants.USERNAME, authUser.getUsername());
            String newAccessToken = JwtUtils.generateToken(newClaims, JwtConstants.TEMP_TOKEN_EXPIRATION);
            redisTokenUtils.saveAccessToken(userId, newAccessToken, JwtConstants.TEMP_TOKEN_EXPIRATION / 1000);
            return newAccessToken;
        } catch (Exception e) {
            log.error("刷新 token 失败", e);
            return null;
        }
    }

    /**
     * 处理退出登录逻辑。
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = null;
        String authorizationHeader = request.getHeader(RequestConstants.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(RequestConstants.BEARER)) {
            accessToken = authorizationHeader.substring(7);
        }
        if (accessToken != null && !accessToken.isEmpty()) {
            Claims claims = JwtUtils.parseToken(accessToken);
            Integer userId = Integer.valueOf(claims.get(JwtConstants.USER_ID).toString());
            try {
                redisTokenUtils.deleteAccessToken(userId);
                log.info("删除 Redis 中的 accessToken 成功");
            } catch (Exception e) {
                log.error("删除 Redis 中的 accessToken 失败", e);
            }
        }

        String refreshToken = cookieUtils.getRefreshTokenFromCookie(request);
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                Claims claims = JwtUtils.parseToken(refreshToken);
                Integer userId = Integer.valueOf(claims.get(JwtConstants.USER_ID).toString());
                redisTokenUtils.deleteRefreshToken(userId);
                log.info("删除 Redis 中的 refreshToken 成功");
            } catch (Exception e) {
                log.error("删除 Redis 中的 refreshToken 失败", e);
            }
        }

        cookieUtils.clearRefreshTokenCookie(response);
    }
}