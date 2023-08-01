package com.lazy.cache.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.lazy.cache.annotation.YsMemoryCache;
import com.lazy.cache.support.YsBusinessKeyProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author ：cy
 * @description：内存缓存切面
 * 允许存null
 * @date ：2021/12/10 14:29
 */
@Slf4j
@Aspect
@Component
public class YsMemoryCacheAspect {

    private static final Object PRESENT = new Object();

    @Autowired
    @Qualifier("caffeineCache")
    Cache<Object,Object> caffeineCache;

    @Autowired
    YsBusinessKeyProvider ysBusinessKeyProvider;

    @Pointcut("@annotation(com.lazy.cache.annotation.YsMemoryCache)")
    public void memoryCachePointCut() {
    }

    @Around("memoryCachePointCut() && @annotation(memoryCache)")
    public Object around(ProceedingJoinPoint point, YsMemoryCache memoryCache) throws Throwable{
        String keyName = memoryCache.name() + ysBusinessKeyProvider.getKeyName(point, memoryCache.keys());
        Object cacheResult = caffeineCache.getIfPresent(keyName);
        if (PRESENT == cacheResult){
            return null;
        }
        if (null != cacheResult){
            return cacheResult;
        }

        Object result = point.proceed();
        try {
            caffeineCache.put(keyName, result == null ? PRESENT : result);
        } catch (Exception e) {
            log.error(String.format("warnMessage:YsMemoryCache缓存失败:name:%s,result:%s,errorMessage:%s"
                , memoryCache.name()
                , result
                , e.getMessage()
            ));
        }
        return result;
    }

}
