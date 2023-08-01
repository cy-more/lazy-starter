package com.lazy.rocketmq.tlog;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.lazy.rocketmq.support.YsAbstractMqProducer;
import com.lazy.rocketmq.util.MqUtil;
import com.yomahub.tlog.core.mq.TLogMqWrapBean;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ：cy
 * @description：默认mq生产者
 * @date ：2022/1/20 17:24
 */
@Slf4j
public class YsTlogMqProducer extends YsAbstractMqProducer {

    public YsTlogMqProducer(ProducerBean producer) {
        super(producer);
    }

    /**
     * 初始化message
     * @param topic
     * @param tag
     * @param msgBody
     * @return
     */
    @Override
    public Message initMessage(String topic, String tag, String key, Object msgBody){
        TLogMqWrapBean<?> tLogMqWrap = new TLogMqWrapBean<>(msgBody);
        Message message = new Message();
        message.setTopic(topic);
        message.setTag(tag);
        message.setKey(key);
        message.setBody(MqUtil.toByte(tLogMqWrap));
        return message;
    }


}
