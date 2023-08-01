package com.lazy.cache.listener;

import com.lazy.cache.entity.CacheMessage;
import com.lazy.cache.support.YsMultiCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author ：cy
 * @description：本地缓存更新监听
 * @date ：2022/7/19 10:08
 */
@Slf4j
public class CaffeineUpdateListener implements MessageListener {
    private RedisTemplate<Object, Object> redisTemplate;

    private YsMultiCacheManager multiCacheManager;

    public CaffeineUpdateListener(RedisTemplate<Object, Object> cacheRedisTemplate,
                                  YsMultiCacheManager multiCacheManager) {
        super();
        this.redisTemplate = cacheRedisTemplate;
        this.multiCacheManager = multiCacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Object obj = redisTemplate.getValueSerializer().deserialize(message.getBody());
        CacheMessage cacheMessage = (CacheMessage) obj;
        if (cacheMessage == null){
            log.warn("推送消息为空");
            return;
        }
        multiCacheManager.clearCaffeine(cacheMessage.getName(), cacheMessage.getKey());
    }
}
