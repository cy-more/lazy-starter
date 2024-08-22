package com.lazy.rocketmq.support.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;

/**
 * @author ：cy
 * @description：消费
 * @date ：2021/11/2 10:09
 */
public interface YsRocketMqConsumer {


    Action consume(final Message message, final ConsumeContext context);

    /**
     * 个性化定制消费者
     * 配置参考：PropertyKeyConst
     * @param consumerBean 原生消费者类
     */
    default void prepareStart(ConsumerBean consumerBean){

    }
}
