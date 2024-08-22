package com.lazy.limit.support;

import com.lazy.limit.constant.RateLimiterConstants;
import com.lazy.limit.entity.RedisLimiterVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;
import java.util.List;

/**
 * @author ：cy
 * @description：限流器
 * 额外功能：
 * 1.支持令牌回滚
 * 2.额外支持查询当前桶大小
 * 或直接使用redisson
 * copy from org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
 * See https://stripe.com/blog/rate-limiters
 * and https://gist.github.com/ptarjan/e38f45f2dfe601419ca3af937fff574d#file-1-check_request_rate_limiter-rb-L11-L34.
 * @date ：2022/5/10 20:41
 */
public class RedisRateLimiter {
    private Log log = LogFactory.getLog(getClass());

    private final StringRedisTemplate redisTemplate;

    private RedisScript<List<Long>> script;

    public RedisRateLimiter(StringRedisTemplate redisTemplate, RedisScript<List<Long>> script) {
        this.redisTemplate = redisTemplate;
        this.script = script;
    }

    public void isAllowedWithWarn(String keyId, int replenishRate, int burstCapacity){
        isAllowedWithWarn(keyId, replenishRate, burstCapacity, 1, 0);
    }

    /**
     * 获取令牌 阻塞/告警
     * @param keyId
     * @param replenishRate 令牌生成速率
     * @param burstCapacity 桶大小
     * @param requestedTokens 令牌单次请求量
     * @param leaseTime 超时 单位:s 建议不要过长
     */
    public void isAllowedWithWarn(String keyId, int replenishRate, int burstCapacity, int requestedTokens, long leaseTime){
        do {
            RedisLimiterVO vo = isAllowed(keyId, replenishRate, burstCapacity, requestedTokens);
            if (vo.getIsAllowed()){
                return;
            }else{
                //需要等待的时间
                long sleepTime = (requestedTokens - vo.getTokensLeft())*replenishRate;
                //超过最大阻塞时间
                if (sleepTime > leaseTime){
                    break;
                }else{
                    leaseTime = leaseTime - sleepTime;
                    try {
                        Thread.sleep(sleepTime*1000);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }while (true);

        //超过等待时间还未获取到令牌
        log.error("warnMessage:request is limited,key：" + keyId);
        throw new LimitBizException(RateLimiterConstants.ERROR_CODE_LIMIT, "request is limited");
    }

    /**
     * 获取令牌
     * @param keyId
     * @param replenishRate 令牌生成速率 How many requests per second do you want a user to be allowed to do?
     * @param burstCapacity 桶大小 How much bursting do you want to allow?
     * @param requestedTokens 令牌单次请求量 How many tokens are requested per request?
     * @return
     */
    public RedisLimiterVO isAllowed(String keyId, int replenishRate, int burstCapacity, int requestedTokens){
        try {
            // The arguments to the LUA script. time() returns unixtime in seconds.
            List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "", "", requestedTokens + "");
            // allowed, tokens_left = redis.eval(SCRIPT, keys, args)
            List<Long> results = this.redisTemplate.execute(this.script, getKeys(keyId), replenishRate + "", burstCapacity + "", "", requestedTokens + "");

            return new RedisLimiterVO(results.get(0) == 1L, results.get(1));
        }catch (Exception e) {
            /*
             * We don't want a hard dependency on Redis to allow traffic. Make sure to set
             * an alert so you know if this is happening too much. Stripe's observed
             * failure rate is 0.01%.
             */
            log.error("warnMessage:Error determining if user allowed from redis", e);
        }
        return new RedisLimiterVO(true, 0L);
    }

    /**
     * 获取剩余令牌数
     * @param keyId
     * @param replenishRate
     * @param burstCapacity
     * @return
     */
    public Long getCurrentTokenNum(String keyId, int replenishRate, int burstCapacity){
        return isAllowed(keyId, replenishRate, burstCapacity, 0).getTokensLeft();
    }

    static List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }
}
