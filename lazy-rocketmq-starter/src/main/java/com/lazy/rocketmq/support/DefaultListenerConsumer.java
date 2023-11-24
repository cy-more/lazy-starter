package com.lazy.rocketmq.support;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.lazy.rocketmq.util.MqUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author ：cy
 * @description：默认消费监听
 * @date ：2022/1/4 20:29
 */
@Slf4j
public class DefaultListenerConsumer implements MessageListener {

    /**
     * 慢消费
     */
    final Long SLOW_TIME = 10 * 1000L;

    private final Integer maxRetryTimes;

    YsRocketMqConsumer ysRocketMqConsumer;

    public DefaultListenerConsumer(YsRocketMqConsumer ysRocketMqConsumer, YsRocketMqListenerBean consumer) {
        this.ysRocketMqConsumer = ysRocketMqConsumer;
        this.maxRetryTimes = consumer.getMaxRetryTimes();
    }

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String bodyStr = new String(message.getBody());

        if (StringUtils.isEmpty(bodyStr)){
            log.warn("MQ接收消息为空，直接返回成功");
            return Action.CommitMessage;
        }

        log.debug("mq receive msg:" + message.getMsgID() + message.getTopic() + message.getTag() + "==>"+ bodyStr);

        //执行
        long beginTime = System.currentTimeMillis();
        try {
            return ysRocketMqConsumer.consume(message, context);
        }catch (MqBizException e){
            //预警
            log.error(MqUtil.initMqInfo(message, "消费完成，校验不通过"), e);
            return Action.CommitMessage;
        }catch (Exception e){
            //重试
            if (message.getReconsumeTimes() > maxRetryTimes){
                //记录，预警
                log.error(MqUtil.initMqInfo(message, "warnMessage:消费异常，准备重试,error:" + e.getMessage()), e);
                return Action.ReconsumeLater;
            }
            log.warn(e.getMessage(), e);
            return Action.ReconsumeLater;
        }finally {
            //慢消费记录
            long useTime = System.currentTimeMillis() - beginTime;
            if (useTime > SLOW_TIME){
                log.warn(MqUtil.initMqInfo(message, "慢消费"));
            }else{
                log.debug("consume {} cost: {} ms", message.getMsgID(), useTime);
            }
        }
    }
}
