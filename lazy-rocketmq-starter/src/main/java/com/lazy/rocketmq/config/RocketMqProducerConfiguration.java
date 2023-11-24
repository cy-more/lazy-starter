package com.lazy.rocketmq.config;

import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.lazy.rocketmq.support.producer.YsDefaultMqProducer;
import com.lazy.rocketmq.support.producer.YsMqProducer;
import com.lazy.rocketmq.tlog.YsTlogMqProducer;
import com.lazy.rocketmq.util.MqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：cy
 * @description：mq生产
 * @date ：2022/1/5 9:56
 */
@ConditionalOnProperty(prefix = "mq.rocket.ons", value = {"nameSrvAddr"})
@Configuration
@EnableConfigurationProperties(ConsumerProperties.class)
public class RocketMqProducerConfiguration {

    @Autowired
    ConsumerProperties consumerProperties;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "mq.rocket.ons", value = {"nameSrvAddr"})
    public YsMqProducer defaultMqProducer() {
        ProducerBean producer = new ProducerBean();
        producer.setProperties(MqUtil.getProperty(consumerProperties));
        OrderProducerBean orderProducerBean = new OrderProducerBean();
        orderProducerBean.setProperties(MqUtil.getProperty(consumerProperties));
        return consumerProperties.isEnableTlog()
                ? new YsTlogMqProducer(producer, orderProducerBean)
                : new YsDefaultMqProducer(producer, orderProducerBean);
    }

}
