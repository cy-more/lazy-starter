package com.lazy.rocketmq.support.consumer;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.OrderConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.lazy.rocketmq.config.ConsumerProperties;
import com.lazy.rocketmq.tlog.YsTlogMqConsumer;
import com.lazy.rocketmq.tlog.YsTlogOrderMqConsumer;
import com.lazy.rocketmq.util.MqUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * @author ：cy
 * @description：consumerBean工厂
 * @date ：2022/1/4 20:07
 */
@Component
public class YsRocketMQListenerFactory {

    private final ConsumerProperties consumerProperties;

    public YsRocketMQListenerFactory(ConsumerProperties consumerProperties) {
        this.consumerProperties = consumerProperties;
    }

    public ConsumerBean buildConsumer(YsRocketMqConsumer bean, YsRocketMqListenerConfigBean annotation) {
        String consumerGroup = annotation.getConsumerGroup();
        String topic = annotation.getTopic();
        //配置
        Properties properties = MqUtil.getProperty(consumerProperties);
        properties.setProperty(PropertyKeyConst.GROUP_ID, consumerGroup);

        //订阅关系
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);

        //生成消费者
        //创建消费业务对象
        //决定是否开启tlog
        YsRocketMqConsumer ysRocketMqConsumer = null;
        switch (annotation.getEnableTlog()){
            case NONE:
                ysRocketMqConsumer = consumerProperties.isEnableTlog() ? new YsTlogMqConsumer(bean) : bean;
                break;
            case TRUE:
                ysRocketMqConsumer = new YsTlogMqConsumer(bean);
                break;
            case FALSE:
                ysRocketMqConsumer = bean;
            default:
                break;
        }

        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(properties);
        Map<Subscription, MessageListener> subscriptionTable = Collections.singletonMap(subscription
                , new DefaultListenerConsumer(ysRocketMqConsumer, annotation));
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);


        //特殊定制
        bean.prepareStart(consumerBean);
        consumerBean.start();
        return consumerBean;
    }

    public OrderConsumerBean buildOrderConsumer(YsRocketMqOrderConsumer bean, YsRocketMqOrderListenerConfigBean annotation) {
        String consumerGroup = annotation.getConsumerGroup();
        String topic = annotation.getTopic();
        //配置
        Properties properties = MqUtil.getProperty(consumerProperties);
        properties.setProperty(PropertyKeyConst.GROUP_ID, consumerGroup);

        //订阅关系
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);

        //创建消费业务对象
        //决定是否开启tlog
        YsRocketMqOrderConsumer ysRocketMqOrderConsumer = null;
        switch (annotation.getEnableTlog()){
            case NONE:
                ysRocketMqOrderConsumer = consumerProperties.isEnableTlog() ? new YsTlogOrderMqConsumer(bean) : bean;
                break;
            case TRUE:
                ysRocketMqOrderConsumer = new YsTlogOrderMqConsumer(bean);
                break;
            case FALSE:
                ysRocketMqOrderConsumer = bean;
            default:
                break;
        }

        OrderConsumerBean consumerBean = new OrderConsumerBean();
        consumerBean.setProperties(properties);
        Map<Subscription, MessageOrderListener> subscriptionTable = Collections.singletonMap(subscription
                , new DefaultOrderListenerConsumer(ysRocketMqOrderConsumer, annotation));
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);

        //特殊定制
        bean.prepareStart(consumerBean);
        consumerBean.start();
        return consumerBean;
    }
}
