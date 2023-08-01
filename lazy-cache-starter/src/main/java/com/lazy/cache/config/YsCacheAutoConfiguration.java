package com.lazy.cache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lazy.cache.support.YsBusinessKeyProvider;
import com.lazy.cache.support.YsCacheExpressionEvaluator;
import com.lazy.cache.support.YsMultiCacheManager;
import com.lazy.cache.util.YsRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author ：cy
 * @description：缓存配置
 * @date ：2021/9/28 20:37
 */
@Slf4j
@AutoConfigureAfter(YsRedisAutoConfiguration.class)
@EnableConfigurationProperties({CacheProperties.class, YsMultiCacheProperties.class})
@ComponentScan({"com.lazy.cache.aspect"})
@Import({YsBusinessKeyProvider.class, YsCacheExpressionEvaluator.class})
@EnableCaching
@Configuration
public class YsCacheAutoConfiguration extends CachingConfigurerSupport {

    @Autowired
    CacheProperties cacheProperties;

    @Autowired
    YsMultiCacheProperties multiCacheProperties;

    @Bean
    @ConditionalOnMissingBean(name = "cacheKeyGenerator")
    public KeyGenerator cacheKeyGenerator() {
        return new SimpleKeyGenerator();
    }

    /**
     * 异常处理
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "errorHandler")
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                cacheErrorException(exception, key);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                cacheErrorException(exception, key);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                cacheErrorException(exception, key);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                cacheErrorException(exception, null);
            }
        };
    }

    protected void cacheErrorException(Exception exception, Object key){
        log.error("warnMessage: 缓存异常：key=[{}]", key, exception);
    }


    /**
     * 内存缓存
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "caffeineCache")
    public Cache<Object, Object> caffeineCache(){
        String specification = cacheProperties.getCaffeine().getSpec();
        if (StringUtils.hasText(specification)) {
            return Caffeine.from(specification).build();
        }else {
            //默认
            return Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .maximumSize(10_000)
                    .build();
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.cache.multi", value = {"topic"})
    public RedisTemplate<Object, Object> cacheRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return YsRedisUtil.initCacheRedisTemplate(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.cache.multi", value = {"topic"})
    public YsMultiCacheManager cacheManager(RedisTemplate<Object, Object> cacheRedisTemplate
        , Cache<Object, Object> caffeineCache) {
        return new YsMultiCacheManager(cacheProperties.getCacheNames()
                , caffeineCache
                , getRedisConfig()
                , cacheRedisTemplate
                , multiCacheProperties);
    }

    /**
     * 组装redis-cache配置
     * @return
     */
    private RedisCacheConfiguration getRedisConfig(){
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        //default配置缺省
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config
                // 设置key序列化器
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(YsRedisUtil.keySerializer()))
                // 设置value序列化器
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer((YsRedisUtil.valueSerializer())));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (multiCacheProperties.getKeyPrefix() == null) {
            config = config.disableKeyPrefix();
        }else {
            config = config.prefixKeysWith(multiCacheProperties.getKeyPrefix());
        }
        if (!multiCacheProperties.getIsAllowNullValues()) {
            config = config.disableCachingNullValues();
        }
        return config;
    }




}
