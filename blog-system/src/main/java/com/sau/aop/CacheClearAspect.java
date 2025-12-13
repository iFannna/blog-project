package com.sau.aop;

import com.sau.annotation.CacheEvictByPrefix;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class CacheClearAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 匹配自定义注解的切点
    @Pointcut("@annotation(com.sau.annotation.CacheEvictByPrefix)")
    public void clearCachePointcut() {}

    /**
     * 方法执行成功后，删除Redis中指定前缀的所有Key
     */
    @AfterReturning(value = "clearCachePointcut() && @annotation(cacheEvictByPrefix)", returning = "result")
    public void afterReturning(CacheEvictByPrefix cacheEvictByPrefix, Object result) {
        String keyPrefix = cacheEvictByPrefix.value();
        if (keyPrefix == null || keyPrefix.isEmpty()) {
            log.warn("缓存清除前缀为空，跳过清除操作");
            return;
        }
        // 执行前缀删除逻辑
        deleteKeysByPrefix(keyPrefix);
    }

    /**
     * 核心方法：删除Redis中指定前缀的所有Key（兼容单机/集群）
     */
    private void deleteKeysByPrefix(String prefix) {
        // 拼接模糊匹配规则：prefix*
        String pattern = prefix + "*";
        try {
            // Scan扫描（推荐，避免keys命令阻塞Redis）
            Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Set<String> keySet = new HashSet<>();
                // 开启scan迭代器，匹配指定前缀的Key
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                        .match(pattern)
                        .count(100)  // 每次扫描1000条，可根据数据量调整
                        .build());
                while (cursor.hasNext()) {
                    // 字节数组转字符串（匹配RedisTemplate的序列化规则）
                    keySet.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
                cursor.close();
                return keySet;
            });

            if (CollectionUtils.isEmpty(keys)) {
                log.info("未找到前缀为{}的Redis Key，无需清除", prefix);
                return;
            }

            // 批量删除Key（分批处理，避免单次删除过多Key阻塞）
            int batchSize = 100;
            List<List<String>> batches = keys.stream()
                    .collect(Collectors.groupingBy(i -> i.hashCode() / batchSize))
                    .values()
                    .stream()
                    .toList();

            for (List<String> batch : batches) {
                redisTemplate.delete(batch);
                log.info("清除Redis Key：{}", batch);
            }
        } catch (Exception e) {
            log.error("清除{}的Redis缓存失败", prefix, e);
        }
    }
}