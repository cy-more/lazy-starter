package com.lazy.cache.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.lazy.cache.annotation.YsRedisCache;
import com.lazy.cache.support.YsBusinessKeyProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.support.NullValue;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * @author ：cy
 * @description：redis缓存切面
 * @date ：2021/12/10 14:29
 */
@Slf4j
@ConditionalOnProperty(prefix = "spring.cache.multi", value = {"topic"})
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Component
public class YsRedisCacheAspect {

    /**
     * 缓存key前缀
     */
    private static final String KEY_PREFIX = "lazy_cache:";

    @Autowired
    RedisTemplate<Object, Object> cacheRedisTemplate;

    @Autowired
    YsBusinessKeyProvider ysBusinessKeyProvider;

    @Autowired
    @Qualifier("taskExecutor")
    Executor taskExecutor;

    @Pointcut("@annotation(com.lazy.cache.annotation.YsRedisCache)")
    public void redisCachePointCut() {
    }

    @Around("redisCachePointCut() && @annotation(redisCache)")
    public Object around(ProceedingJoinPoint point, YsRedisCache redisCache) throws Throwable{
        String keyName = KEY_PREFIX + redisCache.value() + ysBusinessKeyProvider.getKeyName(point, redisCache.key());

        Object result = null;
        try {
            result = cacheRedisTemplate.opsForValue().get(keyName);
        } catch (Exception e) {
            cacheErrorHandle(e, "获取缓存失败", redisCache);
        }
        if (ObjectUtil.isEmpty(result)){
            result = point.proceed();

            Object cacheVal = result != null ? result : NullValue.INSTANCE;
            taskExecutor.execute(() -> {
                try {
                    cacheRedisTemplate.opsForValue().set(keyName, cacheVal
                            , redisCache.timeout(), redisCache.unit());
                } catch (Exception e) {
                    cacheErrorHandle(e, cacheVal, redisCache);
                }
            });
        }else if(result == NullValue.INSTANCE){
            result = null;
        }

        return result;
    }


    private void cacheErrorHandle(Throwable e, Object result, YsRedisCache redisCache){
        log.error(String.format("warnMessage:YsRedisCache缓存失败:name:%s,result:%s,errorMessage:%s"
                , redisCache.value()
                , result
                , e.getMessage()
        ));
    }

}
