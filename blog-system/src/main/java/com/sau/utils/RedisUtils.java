package com.sau.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用操作工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean set(String key, Object value) {
        return set(key, value, 0);
    }

    public boolean set(String key, Object value, long timeoutSeconds) {
        try {
            if (timeoutSeconds > 0) {
                redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("Redis set error, key: {}, value: {}", key, value, e);
            return false;
        }
    }

    public Object get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get error, key: {}", key, e);
            return null;
        }
    }

    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis delete error, key: {}", key, e);
            return false;
        }
    }

    public long delete(Collection<String> keys) {
        try {
            Long deletedCount = redisTemplate.delete(keys);
            return deletedCount == null ? 0 : deletedCount;
        } catch (Exception e) {
            log.error("Redis batch delete error, keys: {}", keys, e);
            return 0;
        }
    }

    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis exists error, key: {}", key, e);
            return false;
        }
    }

    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire == null ? -2 : expire;
        } catch (Exception e) {
            log.error("Redis getExpire error, key: {}", key, e);
            return -2;
        }
    }

    public long increment(String key) {
        return increment(key, 1);
    }

    public long increment(String key, long delta) {
        try {
            Long value = redisTemplate.opsForValue().increment(key, delta);
            return value == null ? 0 : value;
        } catch (Exception e) {
            log.error("Redis increment error, key: {}, delta: {}", key, delta, e);
            return 0;
        }
    }

    public long decrement(String key) {
        return decrement(key, 1);
    }

    public long decrement(String key, long delta) {
        try {
            Long value = redisTemplate.opsForValue().decrement(key, delta);
            return value == null ? 0 : value;
        } catch (Exception e) {
            log.error("Redis decrement error, key: {}, delta: {}", key, delta, e);
            return 0;
        }
    }
}
