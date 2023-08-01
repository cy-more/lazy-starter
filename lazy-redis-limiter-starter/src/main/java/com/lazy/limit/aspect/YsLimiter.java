package com.lazy.limit.aspect;

import java.lang.annotation.*;

/**
 * @author ：cy
 * @description：限流注解
 * 流量峰值 capacity/request 次/s
 * 流量均值 rate/request 次/s
 * @date ：2022/05/10 16:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface YsLimiter {

    /**
     * 同key共限
     * @return
     */
    String key();

    /**
     * 取令牌速率
     * @return
     */
    int request() default 1;

    /**
     * 生成令牌速率
     * @return
     */
    int rate();

    /**
     * 桶大小
     * @return
     */
    int capacity();

    /**
     * 超时时间
     * @return
     */
    long leaseTime() default 0L;
}
