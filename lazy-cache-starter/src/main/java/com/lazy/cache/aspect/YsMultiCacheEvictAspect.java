package com.lazy.cache.aspect;

import com.lazy.cache.annotation.YsMultiCacheEvict;
import com.lazy.cache.support.YsBusinessKeyProvider;
import com.lazy.cache.support.YsCacheExpressionEvaluator;
import lombok.Data;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：cy
 * @description：内存缓存切面
 * @date ：2021/12/10 14:29
 */
@Slf4j
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Component
public class YsMultiCacheEvictAspect {
    /**
     * spel解析用
     */
    private static final Object PRESENT = new Object();
    /**
     * spel解析用
     */
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Autowired
    private YsBusinessKeyProvider ysBusinessKeyProvider;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private YsCacheExpressionEvaluator expressionEvaluator;


    @Pointcut("@annotation(com.lazy.cache.annotation.YsMultiCacheEvict)")
    public void multiCachePointCut() {
    }

    @Around("multiCachePointCut() && @annotation(multiCacheEvict)")
    public Object around(ProceedingJoinPoint point, YsMultiCacheEvict multiCacheEvict) throws Throwable{
        HandleBeforeResult handleBeforeResult = cacheHandleBefore(point, multiCacheEvict);

        Object result = point.proceed();

        cacheHandleAfter(handleBeforeResult.getCaches(), handleBeforeResult.getKey(), multiCacheEvict);
        return result;
    }

    public HandleBeforeResult cacheHandleBefore(ProceedingJoinPoint point, YsMultiCacheEvict multiCacheEvict) {
        HandleBeforeResult handleBeforeResult = new HandleBeforeResult();
        try{
            Method method = ysBusinessKeyProvider.getMethod(point);
            MethodBasedEvaluationContext methodContext = new MethodBasedEvaluationContext(PRESENT, method, point.getArgs(), parameterNameDiscoverer);

            handleBeforeResult.setCaches(getCache(multiCacheEvict.value(), methodContext));
            handleBeforeResult.setKey(expressionEvaluator.getValUnCache(multiCacheEvict.key(), methodContext));
        }catch (Exception e){
            log.error("缓存删除不生效，error:" + e.getMessage(), e);
        }
        return handleBeforeResult;
    }

    public void cacheHandleAfter(List<Cache> caches, String key, YsMultiCacheEvict multiCacheEvict){
        try {
            if (caches == null) {
                log.info("缓存删除不生效，name:" + multiCacheEvict.value());
                return;
            }
            //延迟双删
//        removeCache(caches, key);
            //极致的话：
            //---补充延迟删除
            removeCache(caches, key);
        }catch (Exception e){
            log.error("缓存删除不生效，error:" + e.getMessage(), e);
        }
    }

    /**
     * 删除缓存
     * 支持模糊name删除
     * @param caches
     * @param key
     */
    @Async
    public void removeCache(List<Cache> caches, String key){
        try {
            if (StringUtils.isBlank(key)){
                caches.forEach(Cache::clear);
            }else {
                caches.forEach(cache -> cache.evict(key));
            }
        } catch (Exception e) {
            log.error(String.format("warnMessage:YsMultiCache缓存删除失败:name:%s,key:%s,errorMessage:%s"
                    , caches.stream().map(Cache::getName).collect(Collectors.joining(","))
                    , key
                    , e.getMessage()
            ));
        }
    }

    /**
     * 获取cache
     * @param cacheName
     * @param methodContext
     * @return
     */
    private List<Cache> getCache(String cacheName, MethodBasedEvaluationContext methodContext){
        if(StringUtils.isBlank(cacheName)){
            return null;
        }
        //先匹配前缀表达式
        if (cacheName.endsWith("*")){
            String prefixCacheNameStr = expressionEvaluator.getValUnCache(cacheName.substring(0, cacheName.length() - 1), methodContext);
            if (prefixCacheNameStr == null){
                return null;
            }
            return cacheManager.getCacheNames()
                    .stream()
                    .filter(name -> name.startsWith(prefixCacheNameStr))
                    .map(name -> cacheManager.getCache(name))
                    .collect(Collectors.toList());
        //匹配spel表达式
        }else{
            String val = expressionEvaluator.getValUnCache(cacheName, methodContext);
            if (val == null){
                return null;
            }
            return Collections.singletonList(cacheManager.getCache(val));
        }

    }

    @Data
    static class HandleBeforeResult {
        List<Cache> caches;
        String key;
    }
}
