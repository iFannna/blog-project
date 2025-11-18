package com.sau.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisTokenUtils {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 保存refreshToken到Redis
     */
    public void saveRefreshToken(Integer userId, String refreshToken, Long expiration) {
        String key = "refresh_token:" + userId;
        redisUtils.set(key, refreshToken, expiration);
        log.info("保存refreshToken到Redis: {}={}", key, refreshToken);
    }

    /**
     * 从Redis获取refreshToken
     */
    public String getRefreshToken(Integer userId) {
        String key = "refresh_token:" + userId;
        Object value = redisUtils.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 删除Redis中的refreshToken
     */
    public void deleteRefreshToken(Integer userId) {
        String key = "refresh_token:" + userId;
        redisUtils.delete(key);
    }

    /**
     * 保存accessToken到Redis
     */
    public void saveAccessToken(Integer userId, String accessToken, Long expiration) {
        String key = "access_token:" + userId;
        redisUtils.set(key, accessToken, expiration);
        log.info("保存accessToken到Redis: {}={}", key, accessToken);
    }

    /**
     * 从Redis获取accessToken
     */
    public String getAccessToken(Integer userId) {
        String key = "access_token:" + userId;
        Object value = redisUtils.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 删除Redis中的accessToken
     */
    public void deleteAccessToken(Integer userId) {
        String key = "access_token:" + userId;
        redisUtils.delete(key);
    }

    /**
     * 将accessToken加入黑名单
     */
    public void addAccessTokenToBlacklist(String accessToken, long expiration) {
        String key = "access_token_blacklist:" + accessToken;
        redisUtils.set(key, "invalid", expiration);
    }
}
