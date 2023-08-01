package com.lazy.limit.config;

import com.lazy.limit.aspect.YsLimitAspect;
import com.lazy.limit.constant.RateLimiterConstants;
import com.lazy.limit.support.RedisRateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

/**
 * @author ：cy
 * @description：redis配置
 * @date ：2022/05/10 16:40
 */
@Import(YsLimitAspect.class)
@ConditionalOnBean(RedisTemplate.class)
@Configuration
public class YsRedisLimiterAutoConfiguration {

    /**
     * 限流redistemplate
     * 固定序列化
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * 限流脚本
     * @return
     */
    @Bean
    @SuppressWarnings("unchecked")
    public RedisScript redisRequestRateLimiterScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("META-INF/scripts/request_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }


    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiter redisRateLimiter(StringRedisTemplate stringRedisTemplate,
                                             @Qualifier(RateLimiterConstants.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript) {
        return new RedisRateLimiter(stringRedisTemplate, redisScript);
    }
}
