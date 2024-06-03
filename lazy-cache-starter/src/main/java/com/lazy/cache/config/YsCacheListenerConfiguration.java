package com.lazy.cache.config;

import com.lazy.cache.listener.CaffeineUpdateListener;
import com.lazy.cache.support.YsMultiCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.Assert;

/**
 * @author ：cy
 * @description：缓存更新监听
 * @date ：2022/7/19 10:18
 */
@Configuration
@ConditionalOnBean(YsMultiCacheManager.class)
@AutoConfigureAfter({YsCacheAutoConfiguration.class})
@EnableConfigurationProperties(YsMultiCacheProperties.class)
@ConditionalOnMissingBean(YsCacheListenerConfiguration.class)
public class YsCacheListenerConfiguration {

    @Autowired
    YsMultiCacheProperties multiCacheProperties;

    public YsMultiCacheProperties getMultiCacheProperties() {
        return multiCacheProperties;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.cache.multi", name = "topic")
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisTemplate<Object, Object> cacheRedisTemplate, YsMultiCacheManager multiCacheManager) {
        Assert.notNull(cacheRedisTemplate.getConnectionFactory(), "redisConnectionFactory of cacheRedisTemplate must not be null!");
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(cacheRedisTemplate.getConnectionFactory());
        CaffeineUpdateListener cacheMessageListener = new CaffeineUpdateListener(cacheRedisTemplate, multiCacheManager);
        redisMessageListenerContainer.addMessageListener(cacheMessageListener, new ChannelTopic(getMultiCacheProperties().getTopic()));
        return redisMessageListenerContainer;
    }
}
