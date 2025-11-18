package com.sau.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置键值对并指定过期时间
     */
    public boolean set(String key, Object value) {
        return set(key, value, 0);
    }

    /**
     * 设置键值对并指定过期时间（单位：秒）
     */
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

    /**
     * 获取键对应的值
     */
    public Object get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get error, key: {}", key, e);
            return null;
        }
    }

    /**
     * 删除指定键
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis delete error, key: {}", key, e);
            return false;
        }
    }

    /**
     * 批量删除键
     */
    public long delete(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("Redis batch delete error, keys: {}", keys, e);
            return 0;
        }
    }

    /**
     * 判断键是否存在
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis exists error, key: {}", key, e);
            return false;
        }
    }

    /**
     * 获取键的剩余过期时间（单位：秒）
     * @return 剩余时间（-1：永不过期；-2：键不存在）
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire == null ? -2 : expire;
        } catch (Exception e) {
            log.error("Redis getExpire error, key: {}", key, e);
            return -2;
        }
    }

    /**
     * 自增（默认步长1）
     */
    public long increment(String key) {
        return increment(key, 1);
    }

    /**
     * 自增（指定步长）
     */
    public long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis increment error, key: {}, delta: {}", key, delta, e);
            return 0;
        }
    }

    /**
     * 自减（默认步长1）
     */
    public long decrement(String key) {
        return decrement(key, 1);
    }

    /**
     * 自减（指定步长）
     */
    public long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("Redis decrement error, key: {}, delta: {}", key, delta, e);
            return 0;
        }
    }
}