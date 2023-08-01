package com.lazy.cache.support;

import com.lazy.cache.config.YsMultiCacheProperties;
import lombok.Data;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：cy
 * @description：多级缓存管理器
 * 允许动态创建缓存
 * 允许保存null值
 * @date ：2022/7/14 19:29
 */
@Data
public class YsMultiCacheManager implements CacheManager {

    private YsMultiCacheProperties multiCacheProperties;

    RedisCacheConfiguration redisCacheConfiguration;

    RedisTemplate<Object, Object> cacheRedisTemplate;

    RedisCacheWriter redisCacheWriter;

    com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeine;

    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

    public YsMultiCacheManager(List<String> cacheNames, com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeine
            , RedisCacheConfiguration redisCacheConfiguration
            , RedisTemplate<Object, Object> cacheRedisTemplate
            , YsMultiCacheProperties multiCacheProperties) {
        Assert.notNull(cacheRedisTemplate, "cacheRedisTemplate must not be null!");
        Assert.notNull(cacheRedisTemplate.getConnectionFactory(), "redisConnectionFactory of cacheRedisTemplate must not be null!");
        this.redisCacheConfiguration = redisCacheConfiguration;
        this.cacheRedisTemplate = cacheRedisTemplate;
        this.redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(cacheRedisTemplate.getConnectionFactory());
        this.caffeine = caffeine;
        this.multiCacheProperties = multiCacheProperties;

        if (cacheNames != null) {
            for (String name : cacheNames) {
                this.cacheMap.put(name, createMultiCache(name));
            }
        }
    }

    @Override
    public Cache getCache(String name) {
        return this.cacheMap.computeIfAbsent(name, this::createMultiCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

    private Cache createMultiCache(String name){
        CaffeineCache caffeineCache = new CaffeineCache(name, caffeine, multiCacheProperties.getIsAllowNullValues());
        RedisCache redisCache = new RedisCache(name, this.redisCacheWriter
                , redisCacheConfiguration != null ? redisCacheConfiguration : RedisCacheConfiguration.defaultCacheConfig());

        return new YsMultiCache(name, caffeineCache, redisCache, cacheRedisTemplate, multiCacheProperties);
    }

    /**
     * 清空本地缓存
     * @param name
     * @param key
     */
    public void clearCaffeine(String name, Object key){
        Cache cache = getCache(name);
        if (cache == null){
            return;
        }

        YsMultiCache multiCache = (YsMultiCache) cache;
        multiCache.clearForCaffeine(key);
    }
}
