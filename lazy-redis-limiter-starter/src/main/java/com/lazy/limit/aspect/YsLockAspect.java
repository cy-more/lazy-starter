package com.lazy.limit.aspect;

import com.lazy.limit.support.YsBusinessKeyProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ：cy
 * @description：redisson切面
 * klock基本是满足的，但配置要单独再配置，故做此改良直接读取redisson配置
 * 基础勉强能用版本
 * @date ：2021/12/10 14:29
 */
@Slf4j
@Aspect
@Component
public class YsLockAspect {

    private static final Object PRESENT = new Object();

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    YsBusinessKeyProvider ysBusinessKeyProvider;

    @Pointcut("@annotation(com.lazy.limit.aspect.YsLock)")
    public void lockPointCut() {
    }

    @Around("lockPointCut() && @annotation(ysLock)")
    public Object around(ProceedingJoinPoint point, YsLock ysLock) throws Throwable{
        String keyName = ysLock.name() + ysBusinessKeyProvider.getKeyName(point, ysLock.keys());
        RLock saveLock = redissonClient.getLock(keyName);
        try {
            if (ysLock.waitTime() == Long.MIN_VALUE) {
                saveLock.lock(ysLock.leaseTime(), ysLock.timeUnit());
            }else{
                saveLock.tryLock(ysLock.waitTime(), ysLock.leaseTime(), ysLock.timeUnit());
            }
            return point.proceed();
        }finally {
            if (saveLock.isLocked() && saveLock.isHeldByCurrentThread()) {
                saveLock.unlock();
            }
        }
    }

}
