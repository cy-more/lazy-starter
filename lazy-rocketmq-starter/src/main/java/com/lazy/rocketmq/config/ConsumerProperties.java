package com.lazy.rocketmq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ：cy
 * @description：消费配置
 * @date ：2021/11/26 18:14
 */
@Data
@ConfigurationProperties(prefix = "mq.rocket.ons")
public class ConsumerProperties {

    private String accessKey;

    private String secretKey;

    private String nameSrvAddr;

    private String sendTimeout;

    private String consumeThreadNums;

    private String maxReconsumeTimes;

    private String maxBatchMessageCount;

    private String suspendTimeMillis;

    /**
     * 指定实例，用于Serverless版实例
     */
    private String instanceId;

    private boolean enableTlog = false;

}
