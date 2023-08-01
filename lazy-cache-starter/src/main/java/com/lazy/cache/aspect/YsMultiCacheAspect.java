package com.lazy.cache.aspect;

import com.lazy.cache.annotation.YsMultiCacheable;
import com.lazy.cache.support.YsBusinessKeyProvider;
import com.lazy.cache.support.YsCacheExpressionEvaluator;
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
        Object result;
        try{
            result = cacheHandle(point, multiCacheable);
        }catch (Exception e){
            log.error("缓存不生效，error:" + e.getMessage(), e);
            result = point.proceed();
        }
        return result;
    }

    private Object cacheHandle(ProceedingJoinPoint point, YsMultiCacheable multiCacheable) throws Throwable{
        Method method = ysBusinessKeyProvider.getMethod(point);
        MethodBasedEvaluationContext methodContext = new MethodBasedEvaluationContext(PRESENT, method, point.getArgs(), parameterNameDiscoverer);
        if (!isConditionPassing(multiCacheable, methodContext)){
            //缓存不生效
            return point.proceed();
        }

        Object cacheName = expressionEvaluator.getValUnCache(multiCacheable.value(), methodContext);
        Object key = expressionEvaluator.getValUnCache(multiCacheable.key(), methodContext);
        if (cacheName == null){
            //缓存不生效
            return point.proceed();
        }
        if (key == null){
            key = PRESENT;
        }

        //获取缓存数据
        Cache cache = cacheManager.getCache(cacheName.toString());

        return cache.get(key, () -> {
            try {
                return point.proceed();
            }catch (Exception e){
                throw e;
            }catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
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


}
