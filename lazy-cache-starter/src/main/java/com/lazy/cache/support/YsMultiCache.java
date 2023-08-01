package com.lazy.cache.support;

import com.lazy.cache.config.YsMultiCacheProperties;
import com.lazy.cache.entity.CacheMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.Callable;

/**
 * @author ：cy
 * @description：多级缓存实现
 * 主要流程：CacheAspectSupport
 * @date ：2022/7/13 14:29
 */
@Slf4j
public class YsMultiCache extends AbstractValueAdaptingCache {

    String name;

    RedisCache redisCache;

    CaffeineCache caffeineCache;

    RedisTemplate<Object, Object> redisTemplate;

    YsMultiCacheProperties multiCacheProperties;

    protected YsMultiCache(String name, CaffeineCache caffeineCache, RedisCache redisCache
            , RedisTemplate<Object, Object> cacheRedisTemplate, YsMultiCacheProperties multiCacheProperties) {
        super(multiCacheProperties.getIsAllowNullValues());
        this.name = name;
        this.caffeineCache = caffeineCache;
        this.redisCache = redisCache;
        this.multiCacheProperties = multiCacheProperties;
        this.redisTemplate = cacheRedisTemplate;
    }

    @Override
    protected Object lookup(Object key) {
        //一级
        String cacheKey = getCacheKey(key);
        Object cVal = caffeineCache.getNativeCache().getIfPresent(cacheKey);
        log.trace(String.format("缓存********查询一级，name:%s,key:%s", name, key));
        if (cVal != null){
            return cVal;
        }
        //二级
        Object rVal = redisCache.lookup(cacheKey);
        log.trace(String.format("缓存********查询二级，name:%s,key:%s", name, key));
        if (rVal != null){
            caffeineCache.put(cacheKey, rVal);
            return rVal;
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public synchronized <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper result = get(key);

        if (result != null) {
            return (T) result.get();
        }

        T value = valueFromLoader(key, valueLoader);
        put(key, value);
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        log.trace(String.format("缓存********put，name:%s,key:%s", name, key));
        Object cacheKey = getCacheKey(key);
        redisCache.put(cacheKey, value);

        push(new CacheMessage(this.name, cacheKey));

        caffeineCache.put(cacheKey, value);
    }

    @Override
    public void evict(Object key) {
        log.trace(String.format("缓存********evict，name:%s,key:%s", name, key));
        Object cacheKey = getCacheKey(key);
        // 先清除redis中缓存数据，然后清除caffeine中的缓存，避免短时间内如果先清除caffeine缓存后其他请求会再从redis里加载到caffeine中
        redisCache.evict(cacheKey);

        push(new CacheMessage(this.name, key));

        caffeineCache.evict(cacheKey);
    }

    @Override
    public void clear() {
        log.trace("缓存********clear，name:" + name);
        redisCache.clear();

        push(new CacheMessage(this.name, null));

        caffeineCache.clear();
    }

    /**
     * 清理本地缓存
     * @param key
     */
    public void clearForCaffeine(Object key) {
        log.trace(String.format("缓存********clearForCaffeine，name:%s,key:%s", name, key));
        if(key == null) {
            caffeineCache.clear();
        } else {
            caffeineCache.evict(getCacheKey(key));
        }
    }

    /**
     * 获取实际存储key
     * @param key
     * @return
     */
    private String getCacheKey(Object key){
        return this.name + ":" + key.toString();
    }

    private static <T> T valueFromLoader(Object key, Callable<T> valueLoader) {
        try {
            return valueLoader.call();
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    /**
     * 缓存变更时通知其他节点清理本地缓存
     * @param message
     */
    private void push(CacheMessage message) {
        redisTemplate.convertAndSend(multiCacheProperties.getTopic(), message);
        log.trace("缓存********推送，msg:" + message);

    }

}
