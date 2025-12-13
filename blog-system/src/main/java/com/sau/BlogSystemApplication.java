package com.sau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;


// @EnableGlobalAuthentication  // 启用Spring Security全局认证
@EnableCaching  // 启用Spring Cache缓存
@SpringBootApplication
public class BlogSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogSystemApplication.class, args);
    }

}
