package com.lazy.cache.util;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author ：cy
 * @description：redis相关
 * @date ：2022/7/19 15:05
 */
public class YsRedisUtil {

    public static RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    public static RedisSerializer<Object> valueSerializer() {
        return RedisSerializer.java(null);
    }

    public static RedisTemplate<Object, Object> initCacheRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // key的序列化类型
        redisTemplate.setKeySerializer(keySerializer());
        // value的序列化类型
        redisTemplate.setValueSerializer(valueSerializer());
        return redisTemplate;
    }
}
