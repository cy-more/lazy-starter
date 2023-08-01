package com.lazy.cache.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author ：cy
 * @description：redis
 * @date ：2021/10/19 16:40
 */
@Configuration
public class YsRedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = RedisTemplate.class, name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // key的序列化类型
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value的序列化类型
        redisTemplate.setValueSerializer(new RedisObjectSerializer());
        return redisTemplate;
    }

}
