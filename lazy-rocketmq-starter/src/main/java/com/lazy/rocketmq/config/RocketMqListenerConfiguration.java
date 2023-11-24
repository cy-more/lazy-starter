package com.lazy.rocketmq.config;

import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.lazy.rocketmq.annotation.YsRocketMqListener;
import com.lazy.rocketmq.support.MqBizException;
import com.lazy.rocketmq.support.YsRocketMQListenerFactory;
import com.lazy.rocketmq.support.YsRocketMqConsumer;
import com.lazy.rocketmq.support.YsRocketMqListenerBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author ：cy
 * @description：整合listener列表
 * @date ：2021/11/16 11:10
 */
@Order(1)
@Slf4j
@ConditionalOnProperty(prefix = "mq.rocket.ons", value = {"nameSrvAddr"})
@EnableConfigurationProperties(ConsumerProperties.class)
@Configuration
public class RocketMqListenerConfiguration implements ApplicationContextAware, SmartInitializingSingleton {
    private ConfigurableApplicationContext applicationContext;

    private StandardEnvironment environment;

    private final AtomicLong counter = new AtomicLong(0);

    private final YsRocketMQListenerFactory listenerFactory;

    public RocketMqListenerConfiguration(StandardEnvironment environment, ConsumerProperties consumerProperties) {
        this.environment = environment;
        this.listenerFactory = new YsRocketMQListenerFactory(consumerProperties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    /**
     * 扫描注解初始化
     */
    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(YsRocketMqListener.class)
                .entrySet().stream().filter(entry -> !ScopedProxyUtils.isScopedTarget(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        beans.forEach(this::registerConsumer);
    }

    /**
     * 加载消费者
     * @param beanName
     * @param bean
     */
    private void registerConsumer(String beanName, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

        if (!YsRocketMqConsumer.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException("消费类必须实现：" + YsRocketMqConsumer.class.getName());
        }

        YsRocketMqListener annotation = clazz.getAnnotation(YsRocketMqListener.class);

        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;

        String registerBeanName = beanName + counter.incrementAndGet();
        genericApplicationContext.registerBean(registerBeanName, ConsumerBean.class,
                () -> listenerFactory.buildConsumer((YsRocketMqConsumer)bean, transListener(environment, annotation)));
        ConsumerBean consumerBean = genericApplicationContext.getBean(registerBeanName,
                ConsumerBean.class);
        if (!consumerBean.isStarted()) {
            try {
                consumerBean.start();
            } catch (Exception e) {
                log.error("Started consumerBean failed. {}", consumerBean, e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 解析注解配置（表达式）
     * @param environment
     * @param annotation
     * @return
     */
    private YsRocketMqListenerBean transListener(StandardEnvironment environment, YsRocketMqListener annotation){
        YsRocketMqListenerBean annotationBean = new YsRocketMqListenerBean();
        String topic = environment.resolvePlaceholders(annotation.topic());
        if (StringUtils.isBlank(topic)){
            throw new MqBizException("topic不能为空或表达式填写不正确");
        }
        annotationBean.setTopic(topic);
        String consumerGroup = environment.resolvePlaceholders(annotation.consumerGroup());
        if (StringUtils.isBlank(consumerGroup)){
            throw new MqBizException("consumerGroup不能为空或表达式填写不正确");
        }
        annotationBean.setConsumerGroup(consumerGroup);
        annotationBean.setSelectorExpression(environment.resolvePlaceholders(annotation.selectorExpression()));
        annotationBean.setEnableTlog(annotation.enableTlog());
        return annotationBean;
    }
}
