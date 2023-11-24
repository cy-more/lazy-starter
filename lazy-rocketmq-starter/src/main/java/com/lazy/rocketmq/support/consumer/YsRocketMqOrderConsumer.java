package com.lazy.rocketmq.support.consumer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.bean.OrderConsumerBean;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.OrderAction;

/**
 * @author ：cy
 * @description：sn消费
 * @date ：2021/11/2 10:09
 */
public interface YsRocketMqOrderConsumer {


    OrderAction consume(final Message message, final ConsumeOrderContext context);

    /**
     * 个性化定制消费者
     * 配置参考：PropertyKeyConst
     * @param consumerBean 原生消费者类
     */
    default void prepareStart(OrderConsumerBean consumerBean){

    }
}
