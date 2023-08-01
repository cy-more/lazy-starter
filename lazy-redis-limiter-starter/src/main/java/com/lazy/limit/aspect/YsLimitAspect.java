package com.lazy.limit.aspect;

import com.lazy.limit.support.RedisRateLimiter;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ：cy
 * @description：限流切面
 * @date ：2022/5/10 11:40
 */
@Aspect
public class YsLimitAspect {

    @Autowired
    RedisRateLimiter rateLimiter;

    @Pointcut("@annotation(com.lazy.limit.aspect.YsLimiter)")
    public void limitPointCut(){}

    @Before("limitPointCut() && @annotation(limiter)")
    public void limitBefore(YsLimiter limiter) {
        rateLimiter.isAllowedWithWarn(limiter.key(), limiter.rate(), limiter.capacity(), limiter.request(), limiter.leaseTime());
    }
}
