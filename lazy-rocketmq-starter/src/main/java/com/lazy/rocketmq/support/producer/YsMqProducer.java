package com.lazy.rocketmq.support.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;

/**
 * @author: cy
 * @description:
 * @date: 2023-06-20 15:19
 **/
public interface YsMqProducer {

    ProducerBean getProducer();



    /**
     * 发送普通消息
     */
    SendResult syncSend(String topic, String tag, Object msgBody);

    SendResult syncSend(String topic, String tag, String key, Object msgBody);

    /**
     * 发送异步消息
     * @param topic
     * @param tag
     * @param msgBody
     */
    void sendAsyncMsg(String topic, String tag, String msgBody);

    void sendAsyncMsg(String topic, String tag, String key, String msgBody);

    /**
     * 发送订单消息
     * @param topic
     * @param tag
     * @param msgBody
     */
    SendResult sendOrder(String topic, String tag, String key, String msgBody, String shardingKey);

    /**
     * 初始化message
     * @param topic
     * @param tag
     * @param msgBody
     * @return
     */
    Message initMessage(String topic, String tag, String key, Object msgBody);

    void start();

    void shutdown();


}
