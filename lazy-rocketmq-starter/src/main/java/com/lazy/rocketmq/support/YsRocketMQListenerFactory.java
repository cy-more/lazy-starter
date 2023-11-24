package com.lazy.rocketmq.support;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.lazy.rocketmq.config.ConsumerProperties;
import com.lazy.rocketmq.tlog.YsTlogMqConsumer;
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

    public ConsumerBean buildConsumer(YsRocketMqConsumer bean, YsRocketMqListenerBean annotation) {
        String consumerGroup = annotation.getConsumerGroup();
        String topic = annotation.getTopic();

        ConsumerBean consumerBean = new ConsumerBean();

        Properties properties = MqUtil.getProperty(consumerProperties);
        properties.setProperty(PropertyKeyConst.GROUP_ID, consumerGroup);
        consumerBean.setProperties(properties);
        //订阅关系
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);

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
        Map<Subscription, MessageListener> subscriptionTable = Collections.singletonMap(subscription
                , new DefaultListenerConsumer(ysRocketMqConsumer, annotation));

        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);

        //特殊定制
        bean.prepareStart(consumerBean);
        consumerBean.start();
        return consumerBean;
    }
}
