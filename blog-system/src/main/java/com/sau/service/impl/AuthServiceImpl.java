package com.sau.service.impl;

import com.sau.constants.JwtConstants;
import com.sau.constants.RequestConstants;
import com.sau.mapper.UserMapper;
import com.sau.pojo.DTO.LoginDTO;
import com.sau.pojo.entity.AuthResult;
import com.sau.pojo.entity.User;
import com.sau.service.AuthService;
import com.sau.utils.CookieUtils;
import com.sau.utils.JwtUtils;
import com.sau.utils.RedisTokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTokenUtils redisTokenUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 注入Cookie工具类
    @Autowired
    private CookieUtils cookieUtils;

    /**
     * 登录相关逻辑
     */
    @Override
    public AuthResult login(LoginDTO loginDTO) {
        // 1. 获取前端传入的账号和明文密码
        String account = loginDTO.getAccount();
        String rawPassword = loginDTO.getPassword();

        // 2. 根据账号查询数据库中的用户
        User dbUser = userMapper.selectByAccount(account);
        if (dbUser == null) {
            log.warn("账号不存在：{}", account);
            return null;
        }

        // 3. 用PasswordEncoder验证明文密码与数据库中的加密密码是否匹配
        boolean matches = passwordEncoder.matches(rawPassword, dbUser.getPassword());
        if (!matches) {
            log.warn("密码错误：{}", account);
            return null;
        }

        // 4. 验证通过，生成access token和refresh token
        log.info("登录成功，用户信息: {}", dbUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtConstants.USER_ID, dbUser.getId());
        claims.put(JwtConstants.USERNAME, dbUser.getUsername());

        // 4.1 access token
        String accessToken = JwtUtils.generateToken(claims, JwtConstants.TEMP_TOKEN_EXPIRATION);
        log.info("生成accessToken：{}", accessToken);
        // 将access token存入Redis
        redisTokenUtils.saveAccessToken(dbUser.getId(), accessToken, JwtConstants.TEMP_TOKEN_EXPIRATION/1000);


        long tokenExpiration;
        // 根据用户是否勾选记住我设置相应过期时间
        if (loginDTO.getRememberMe()) {tokenExpiration = JwtConstants.LONG_TOKEN_EXPIRATION;}
        else {tokenExpiration = JwtConstants.SHORT_TOKEN_EXPIRATION;}

        // 4.2 refresh token
        String refreshToken = JwtUtils.generateToken(claims, tokenExpiration);
        log.info("生成refreshToken：{}", refreshToken);
        // 将refresh token存入Redis
        redisTokenUtils.saveRefreshToken(dbUser.getId(), refreshToken, tokenExpiration/1000);

        // 5. 返回结果

        return new AuthResult(
                dbUser.getId(),
                dbUser.getName(),
                dbUser.getUsername(),
                dbUser.getImage(),
                accessToken,
                refreshToken,
                tokenExpiration);
    }

    /**
     * 刷新AccessToken相关逻辑
     */
    @Override
    public String refreshAccessToken(String refreshToken) {
        try {
            log.info("获取Cookie中的refreshToken：{}", refreshToken);
            // 解析refreshToken
            Claims claims = JwtUtils.parseToken(refreshToken);
            Integer userId = Integer.valueOf(claims.get(JwtConstants.USER_ID).toString());
            String username = claims.get(JwtConstants.USERNAME).toString();

            // 验证refreshToken是否有效

            String storedRefreshToken = redisTokenUtils.getRefreshToken(userId);
            log.info("获取Redis存储的refreshToken：{}", storedRefreshToken);

            if (!refreshToken.equals(storedRefreshToken)) {
                log.warn("无法匹配refreshToken，可能无效或已过期");
                return null;
            }
            log.info("验证refreshToken成功");
            if (redisTokenUtils.getAccessToken(userId) != null){
                log.warn("用户accessToken还未失效，请勿重复申请");
                return null;
            }
            // 生成新的accessToken
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put(JwtConstants.USER_ID, userId);
            newClaims.put(JwtConstants.USERNAME, username);
            String newAccessToken = JwtUtils.generateToken(newClaims, JwtConstants.TEMP_TOKEN_EXPIRATION);
            // 存储新的accessToken
            redisTokenUtils.saveAccessToken(userId, newAccessToken, JwtConstants.TEMP_TOKEN_EXPIRATION/1000);
            log.info("生成新的accessToken：{}", newAccessToken);
            return newAccessToken;

        } catch (Exception e) {
            log.error("刷新token失败", e);
            return null;
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        // 获取accessToken并解析
        String accessToken = null;
        String authorizationHeader = request.getHeader(RequestConstants.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(RequestConstants.BEARER)) {
            accessToken = authorizationHeader.substring(7);
        }
        // 将Redis中的accessToken删除
        if (accessToken != null && !accessToken.isEmpty()) {
            Claims claims = JwtUtils.parseToken(accessToken);
            Integer userId = Integer.valueOf(claims.get(JwtConstants.USER_ID).toString());
            try {
                redisTokenUtils.deleteAccessToken(userId);
                log.info("删除Redis中存储的accessToken成功");
            } catch (Exception e) {
                log.error("删除Redis中存储的accessToken失败", e);
            }
        }


        // 获取refreshToken并解析
        String refreshToken = cookieUtils.getRefreshTokenFromCookie(request);
        // 从Redis删除refreshToken
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                Claims claims = JwtUtils.parseToken(refreshToken);
                Integer userId = Integer.valueOf(claims.get(JwtConstants.USER_ID).toString());
                redisTokenUtils.deleteRefreshToken(userId);
                log.info("删除Redis中存储的refreshToken成功");
            } catch (Exception e) {
                log.error("删除Redis中存储的refreshToken失败", e);
            }
        }

        // 清除Cookie中的refreshToken
        cookieUtils.clearRefreshTokenCookie(response);
    }
}
