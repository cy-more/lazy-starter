package com.lazy.rocketmq.support;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.lazy.rocketmq.util.MqUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ：cy
 * @description：默认mq生产者
 * @date ：2022/1/20 17:24
 */
@Slf4j
public class YsDefaultMqProducer extends YsAbstractMqProducer{

    public YsDefaultMqProducer(ProducerBean producer) {
        super(producer);
    }

    @Override
    public Message initMessage(String topic, String tag, String key, Object msgBody){
        Message message = new Message();
        message.setTopic(topic);
        message.setTag(tag);
        message.setKey(key);
        message.setBody(MqUtil.toByte(msgBody));
        return message;
    }
}
