package com.sau.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

/**
 * Redis序列化器配置类
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate，指定序列化器
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 字符串序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 设置key和value的序列化器
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);

        // 设置hash结构的key和value序列化器
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        // 初始化参数设置
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}