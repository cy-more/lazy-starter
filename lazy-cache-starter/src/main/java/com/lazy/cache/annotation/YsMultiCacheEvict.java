package com.lazy.cache.annotation;

import java.lang.annotation.*;

/**
 * @Author cy
 * @Date 2023/6/2 18:34
 * @Description: 作用和CacheEvict相同，
 * 扩展value的前缀匹配模糊删除 (比spel优先级高:先匹配*再匹配spel）
 * 扩展value的SpEL支持
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface YsMultiCacheEvict {

    String value() default "";

    String key() default "";
}
