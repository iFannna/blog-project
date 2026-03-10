package com.sau.utils;

import com.sau.constants.RedisKeyConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Token Redis 读写工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTokenUtils {

    private final RedisUtils redisUtils;

    public void saveRefreshToken(Integer userId, String refreshToken, Long expiration) {
        String key = RedisKeyConstants.refreshTokenKey(userId);
        redisUtils.set(key, refreshToken, expiration);
        log.info("保存 refreshToken 到 Redis: {}", key);
    }

    public String getRefreshToken(Integer userId) {
        Object value = redisUtils.get(RedisKeyConstants.refreshTokenKey(userId));
        return value != null ? value.toString() : null;
    }

    public void deleteRefreshToken(Integer userId) {
        redisUtils.delete(RedisKeyConstants.refreshTokenKey(userId));
    }

    public void saveAccessToken(Integer userId, String accessToken, Long expiration) {
        String key = RedisKeyConstants.accessTokenKey(userId);
        redisUtils.set(key, accessToken, expiration);
        log.info("保存 accessToken 到 Redis: {}", key);
    }

    public String getAccessToken(Integer userId) {
        Object value = redisUtils.get(RedisKeyConstants.accessTokenKey(userId));
        return value != null ? value.toString() : null;
    }

    public void deleteAccessToken(Integer userId) {
        redisUtils.delete(RedisKeyConstants.accessTokenKey(userId));
    }

    public void addAccessTokenToBlacklist(String accessToken, long expiration) {
        redisUtils.set(RedisKeyConstants.accessTokenBlacklistKey(accessToken), "invalid", expiration);
    }
}
