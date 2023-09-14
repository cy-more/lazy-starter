package com.lazy.cache.aspect;

import com.lazy.cache.annotation.YsMultiCacheable;
import com.lazy.cache.support.YsBusinessKeyProvider;
import com.lazy.cache.support.YsCacheExpressionEvaluator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author ：cy
 * @description：内存缓存切面
 * @date ：2021/12/10 14:29
 */
@Slf4j
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Component
public class YsMultiCacheAspect {

    /**
     * 缓存key前缀
     */
    private static final Object PRESENT = new Object();

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final ExpressionParser parser = new SpelExpressionParser();

    @Autowired
    private YsBusinessKeyProvider ysBusinessKeyProvider;

    @Autowired
    private YsCacheExpressionEvaluator expressionEvaluator;

    @Autowired
    private CacheManager cacheManager;

    @Pointcut("@annotation(com.lazy.cache.annotation.YsMultiCacheable)")
    public void multiCachePointCut() {
    }

    @Around("multiCachePointCut() && @annotation(multiCacheable)")
    public Object around(ProceedingJoinPoint point, YsMultiCacheable multiCacheable) throws Throwable{
        CacheInfo cacheInfo = null;
        try{
            cacheInfo = cacheHandle(point, multiCacheable);
        }catch (Exception e){
            log.error("缓存不生效，error:" + e.getMessage(), e);
        }

        if (cacheInfo == null){
            return point.proceed();
        }else{
            return cacheInfo.getCache().get(cacheInfo.getKey(), () -> {
                try {
                    return point.proceed();
                }catch (Exception e){
                    throw e;
                }catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            });
        }
    }

    private CacheInfo cacheHandle(ProceedingJoinPoint point, YsMultiCacheable multiCacheable) throws Throwable{
        Method method = ysBusinessKeyProvider.getMethod(point);
        MethodBasedEvaluationContext methodContext = new MethodBasedEvaluationContext(PRESENT, method, point.getArgs(), parameterNameDiscoverer);
        if (!isConditionPassing(multiCacheable, methodContext)){
            //缓存不生效
            return null;
        }

        Object cacheName = expressionEvaluator.getValUnCache(multiCacheable.value(), methodContext);
        Object key = expressionEvaluator.getValUnCache(multiCacheable.key(), methodContext);
        if (cacheName == null){
            //缓存不生效
            return null;
        }
        if (key == null){
            key = PRESENT;
        }

        //获取缓存数据
        Cache cache = cacheManager.getCache(cacheName.toString());

        if (cache == null){
            return null;
        }

        return new CacheInfo(cache, key);
    }

    /**
     * 条件是否通过
     * @param cacheable
     * @param method
     * @return
     */
    protected boolean isConditionPassing(YsMultiCacheable cacheable, MethodBasedEvaluationContext method) {
        if (StringUtils.isBlank(cacheable.condition())){
            return true;
        }
        return (Boolean.TRUE.equals(parser.parseExpression(cacheable.condition()).getValue(method, Boolean.class)));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class CacheInfo {
        private Cache cache;

        private Object key;
    }
}
