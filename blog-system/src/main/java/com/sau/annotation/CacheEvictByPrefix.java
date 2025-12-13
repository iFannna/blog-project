package com.sau.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvictByPrefix {
    // 缓存前缀
    String value();
}