package com.lazy.cache.annotation;


import java.lang.annotation.*;

/**
 * @Author cy
 * @Date 2023/5/31 14:34
 * @Description: 作用和Cacheable相同，扩展value的SpEL支持
 * 配合YsMultiCacheEvict 做范围key删除用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface YsMultiCacheable {

    String value() default "";

    String key() default "";

    String condition() default "";

}
