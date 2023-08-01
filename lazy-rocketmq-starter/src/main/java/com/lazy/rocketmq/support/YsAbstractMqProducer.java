package com.lazy.rocketmq.support;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: cy
 * @description: mq生产者抽象父类
 * @date: 2023-06-20 15:00
 **/
@Slf4j
public abstract class YsAbstractMqProducer implements YsMqProducer{

    ProducerBean producer;

    public YsAbstractMqProducer(ProducerBean producer) {
        this.producer = producer;
    }

    public ProducerBean getProducer() {
        return producer;
    }

    public void setProducer(ProducerBean producer) {
        this.producer = producer;
    }

    /**
     * 发送普通消息
     */
    public void syncSend(String topic, String tag, Object msgBody){
        syncSend(topic, tag, null, msgBody);
    }

    public void syncSend(String topic, String tag, String key, Object msgBody) {
        msgLog(topic, tag, msgBody);
        Message message = initMessage(topic, tag, key, msgBody);
        producer.send(message);
    }

    /**
     * 发送异步消息
     * @param topic
     * @param tag
     * @param msgBody
     */
    public void sendAsyncMsg(String topic, String tag, String msgBody){
        sendAsyncMsg(topic, tag, null, msgBody);
    }
    public void sendAsyncMsg(String topic, String tag, String key, String msgBody){
        msgLog(topic, tag, msgBody);
        producer.sendAsync(initMessage(topic, tag, key, msgBody), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                // 处理消息发送成功逻辑
            }

            @Override
            public void onException(OnExceptionContext context) {
                // 处理消息发送异常逻辑
                log.error("mq发送异常：topic:" + topic + "messageId:" + context.getMessageId() + ",error:" + context.getException().getMessage(), context.getException());
            }
        });
    }

    /**
     * 发送订单消息
     * @param topic
     * @param tag
     * @param msgBody
     */
    public void sendOrder(String topic, String tag, String msgBody){
        Message message = initMessage(topic, tag, null, msgBody);
        producer.sendOneway(message);
    }

    public void start(){
        producer.start();
    }

    public void shutdown(){
        log.debug("关闭mq生产者实例success");
        producer.shutdown();
    }


    /**
     * 发送日志
     * @param topic
     * @param tag
     * @param msgBody
     */
    private void msgLog(String topic, String tag, Object msgBody){
        log.info("发送消息：topic:" + topic + ",tag:" + tag + ",body:" + msgBody.toString());
    }

}
