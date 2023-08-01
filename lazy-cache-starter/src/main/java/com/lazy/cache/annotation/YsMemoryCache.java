package com.lazy.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ：cy
 * @description：内存缓存
 * 只缓存在内存，无其他情况优先考虑@Cacheable
 * 不更新缓存
 * @date ：2021/9/28 20:37
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface YsMemoryCache {
    /**
     * 缓存名称
     * @return name
     */
    String name() default "";

    /**
     * 自定义业务key
     * @return keys
     */
    String [] keys() default {};
}
