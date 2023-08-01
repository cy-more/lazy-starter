package com.lazy.limit.constant;

/**
 * @author ：cy
 * @description：限流
 * @date ：2022/5/10 20:41
 */
public final class RateLimiterConstants {

    /**
     * Redis Script name.
     */
    public static final String REDIS_SCRIPT_NAME = "redisRequestRateLimiterScript";

    public static final String ERROR_CODE_LIMIT = "503";

    private RateLimiterConstants() {
    }
}
