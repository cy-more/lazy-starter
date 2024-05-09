package com.lazy.rocketmq.tlog;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.bean.OrderConsumerBean;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.lazy.rocketmq.support.consumer.YsRocketMqOrderConsumer;
import com.lazy.rocketmq.util.MqUtil;
import com.yomahub.tlog.core.mq.TLogMqConsumerProcessor;
import com.yomahub.tlog.core.mq.TLogMqRunner;
import com.yomahub.tlog.core.mq.TLogMqWrapBean;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: cy
 * @description: Tlog包装消费者
 * YsRocketMqConsumer 装饰者
 * @date: 2023-06-20 15:44
 **/
@Slf4j
public class YsTlogOrderMqConsumer implements YsRocketMqOrderConsumer {

    private final YsRocketMqOrderConsumer consumer;
    public YsTlogOrderMqConsumer(YsRocketMqOrderConsumer consumer){
        this.consumer = consumer;
    }

    @Override
    public OrderAction consume(Message message, ConsumeOrderContext context) {
        TLogMqWrapBean tLogMqWrapBean;
        try {
            tLogMqWrapBean = MqUtil.toObj(message.getBody(), TLogMqWrapBean.class);
        }catch (Exception e) {
            return defaultConsume(message, context);
        }
        if (tLogMqWrapBean.getTraceId() == null){
            return defaultConsume(message, context);
        }
        AtomicReference<OrderAction> result = new AtomicReference<>();
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<Object>) o -> {
            //业务操作
            message.setBody(MqUtil.toByte(o));
            result.set(consumer.consume(message, context));
        });
        return result.get();
    }

    @Override
    public void prepareStart(OrderConsumerBean consumerBean) {
        consumer.prepareStart(consumerBean);
    }


    /**
     * tlog消息接收不了转为普通消息
     * @param message
     * @param context
     * @return
     */
    private OrderAction defaultConsume(Message message, ConsumeOrderContext context){
        //不兼容tlog则不做转换处理
        log.warn(String.format("接收的消息不是tlog包装消息!，请和发送端确认是否都是采用开启tlog配置，topic:%s,tag:%s"
                , message.getTopic()
                , message.getTag()));
        return consumer.consume(message, context);
    }
}
