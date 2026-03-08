package com.sau.aop;

import com.sau.annotation.CacheEvictByPrefix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 按前缀清理缓存的切面。
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheClearAspect {

    private static final int SCAN_COUNT = 100;
    private static final int DELETE_BATCH_SIZE = 100;

    private final RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(com.sau.annotation.CacheEvictByPrefix)")
    public void clearCachePointcut() {
    }

    @AfterReturning(value = "clearCachePointcut() && @annotation(cacheEvictByPrefix)", returning = "result")
    public void afterReturning(CacheEvictByPrefix cacheEvictByPrefix, Object result) {
        String keyPrefix = cacheEvictByPrefix.value();
        if (keyPrefix == null || keyPrefix.isEmpty()) {
            log.warn("缓存清除前缀为空，跳过处理");
            return;
        }
        deleteKeysByPrefix(keyPrefix);
    }

    private void deleteKeysByPrefix(String prefix) {
        String pattern = prefix + "*";
        try {
            Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Set<String> keySet = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(SCAN_COUNT).build());
                while (cursor.hasNext()) {
                    keySet.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
                cursor.close();
                return keySet;
            });

            if (CollectionUtils.isEmpty(keys)) {
                log.info("未找到前缀为 {} 的 Redis Key，无需清理", prefix);
                return;
            }

            for (List<String> batch : partition(new ArrayList<>(keys), DELETE_BATCH_SIZE)) {
                redisTemplate.delete(batch);
                log.info("清理 Redis Key: {}", batch);
            }
        } catch (Exception e) {
            log.error("清理 {} 的 Redis 缓存失败", prefix, e);
        }
    }

    private List<List<String>> partition(List<String> source, int batchSize) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < source.size(); i += batchSize) {
            partitions.add(source.subList(i, Math.min(i + batchSize, source.size())));
        }
        return partitions;
    }
}