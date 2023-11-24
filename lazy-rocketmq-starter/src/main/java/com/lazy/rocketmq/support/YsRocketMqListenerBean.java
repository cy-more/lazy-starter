package com.lazy.rocketmq.support;

import com.lazy.rocketmq.annotation.YsRocketMqListener;
import lombok.Data;

/**
 * @author: cy
 * @description: 解析@YsRocketMqListener注解表达式结果配置
 * @date: 2023-11-24 11:10
 **/
@Data
public class YsRocketMqListenerBean {

    /**
     * 主题
     * @return
     */
    private String topic;

    /**
     * 消费组
     * @return
     */
    private String consumerGroup;

    /**
     * tag
     * @return
     */
    private String selectorExpression;

    /**
     * 重试告警次数
     * @return
     */
    private int maxRetryTimes;

    /**
     * 否是开启tlog
     * @return
     */
    YsRocketMqListener.EnableTlogEnum enableTlog;

}
