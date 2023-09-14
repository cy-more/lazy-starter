package com.lazy.rocketmq.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import com.lazy.rocketmq.config.ConsumerProperties;
import com.lazy.rocketmq.constant.MqComConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;

/**
 * @author ：cy
 * @description：mq工具
 * @date ：2021/11/29 14:42
 */
@Slf4j
public class MqUtil {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * 消息信息
     * @param message
     * @return
     */
    public static String initMqInfo(Message message, String content){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageId", message.getMsgID());
        jsonObject.put("message", content);
        jsonObject.put("dateTime", LocalDateTime.now().format(FORMATTER));
        jsonObject.put("topic", message.getTopic());
        jsonObject.put("tag", message.getTag());
        return jsonObject.toJSONString();
    }

    /**
     *
     * @param consumerProperties
     * @return
     */
    public static Properties getProperty(ConsumerProperties consumerProperties){
        //必选配置--
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.AccessKey, consumerProperties.getAccessKey());
        properties.setProperty(PropertyKeyConst.SecretKey, consumerProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, consumerProperties.getNameSrvAddr());
        //可选配置--
        setProperty(properties, PropertyKeyConst.SendMsgTimeoutMillis, consumerProperties.getSendTimeout());
        setProperty(properties, PropertyKeyConst.MaxReconsumeTimes, consumerProperties.getMaxReconsumeTimes());
        setProperty(properties, PropertyKeyConst.MAX_BATCH_MESSAGE_COUNT, consumerProperties.getMaxBatchMessageCount());
        //将消费者线程数 20为默认值
        setProperty(properties, PropertyKeyConst.ConsumeThreadNums, consumerProperties.getConsumeThreadNums());
//        properties.setProperty(PropertyKeyConst.BatchConsumeMaxAwaitDurationInSeconds, consumerProperties.getBatchMaxSize());
        //默认配置--
        properties.setProperty(PropertyKeyConst.AUTO_COMMIT, "false");
        properties.setProperty(PropertyKeyConst.isVipChannelEnabled, "false");
        properties.setProperty(PropertyKeyConst.ALLOCATE_MESSAGE_QUEUE_STRATEGY, "true");
        properties.setProperty(PropertyKeyConst.SuspendTimeMillis, StringUtils.isEmpty(consumerProperties.getSuspendTimeMillis()) ? "1000" : consumerProperties.getSuspendTimeMillis());

        return properties;
    }

    /**
     * 可选配置执行方法
     * @param toProperties
     * @param propertyKey
     * @param o
     */
    private static void setProperty(Properties toProperties, String propertyKey, Object o){
        if (ObjectUtil.isEmpty(o)){
            return;
        }
        toProperties.setProperty(propertyKey, o.toString());
    }

//    public static Properties getProducerProperty(ConsumerProperties consumerProperties){
//
//    }

    /**
     * 对象转byte[]
     * copy from rocketmqUtil.convertToRocketMessage
     * ps: string转换编码默认utf-8
     * @param obj
     * @return
     */
    public static byte[] toByte(Object obj){
        byte[] objByte;
        try {
            if (null == obj) {
                throw new RuntimeException("the object cannot be empty");
            }
            if (obj instanceof String) {
                objByte = ((String) obj).getBytes(StandardCharsets.UTF_8);
            } else if (obj instanceof byte[]) {
                objByte = (byte[]) obj;
            } else {
                String jsonObj = JSONObject.toJSONString(obj);
                if (StringUtils.isEmpty(jsonObj)) {
                    throw new RuntimeException(String.format(
                            "empty after conversion [payloadClass:%s,payloadObj:%s]", obj.getClass(), obj));
                }
                objByte = jsonObj.getBytes(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("convert to byte[] failed.", e);
        }
        return objByte;
    }

    /**
     * mq消息转obj
     * copy from DefaultRocketMQListenerContainer.doConvertMessage
     * @param bytes
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T toObj(byte[] bytes, Class<T> clazz){
        String str = new String(bytes, Charset.forName(MqComConstants.CHARSET));
        if (Objects.equals(clazz, String.class)) {
            return (T)str;
        } else {
            try {
                return JSONObject.parseObject(str, clazz);
            } catch (Exception var4) {
                log.info("convert failed. str:{}, msgType:{}", str, clazz);
                throw new RuntimeException("cannot convert message to " + clazz, var4);
            }
        }
    }

    /**
     * 配置延迟消息
     * @param message
     * @param delayTime 毫秒
     */
    public static void withDelay(Message message, long delayTime){
        message.setStartDeliverTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()
                + delayTime);
    }

    /**
     * 获取原生消费实现类
     * @param consumerBean
     * @return
     */
    public static DefaultMQPushConsumerImpl getConsumerImpl(ConsumerBean consumerBean){
        return  ((DefaultMQPushConsumer) ReflectUtil.getFieldValue(
                ReflectUtil.getFieldValue(consumerBean, "consumer"), "defaultMQPushConsumer"))
                .getDefaultMQPushConsumerImpl();
    }
}
