package com.lazy.kd100.config;

import com.lazy.kd100.core.YsKd100Listener;
import com.lazy.kd100.core.YsKd100MsgListener;
import com.lazy.kd100.support.Kd100Controller;
import com.lazy.kd100.support.Kd100ListenerWebContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ：cy
 * @description：推送监听配置
 * 监听不需要配置任何配置文件
 * @date ：2022/6/10 11:03
 */
@Slf4j
@Import(value = {Kd100ListenerWebContainer.class, Kd100Controller.class})
@Configuration
public class YsKd100PushAutoConfiguration implements ApplicationContextAware, SmartInitializingSingleton {
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    Kd100ListenerWebContainer kd100ListenerWebContainer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    /**
     * 初始化推送监听容器
     */
    @Override
    public void afterSingletonsInstantiated() {
        this.applicationContext.getBeansWithAnnotation(YsKd100MsgListener.class)
            .entrySet().stream()
            .filter(entry -> !ScopedProxyUtils.isScopedTarget(entry.getKey())
                && (entry.getValue() instanceof YsKd100Listener))
            .map(entry -> (YsKd100Listener)entry.getValue())
            .forEach(bean -> {
                YsKd100MsgListener annotation = bean.getClass().getAnnotation(YsKd100MsgListener.class);
                kd100ListenerWebContainer.pollListener(annotation.key(), annotation.state(), bean);
            });
    }
}
