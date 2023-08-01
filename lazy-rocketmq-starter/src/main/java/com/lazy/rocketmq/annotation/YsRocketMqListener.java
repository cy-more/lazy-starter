package com.lazy.rocketmq.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author ：cy
 * @description：消息监听
 * @date ：2021/11/2 10:31
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface YsRocketMqListener {

    /**
     * 主题
     * @return
     */
    String topic() default "";

    /**
     * 消费组
     * @return
     */
    String consumerGroup() default "";

    /**
     * tag
     * @return
     */
    String selectorExpression() default "";

    /**
     * 重试告警次数
     * @return
     */
    int maxRetryTimes() default 1;

    /**
     * 否是开启tlog
     * @return
     */
    EnableTlogEnum enableTlog() default EnableTlogEnum.NONE;

    enum EnableTlogEnum {
        //以配置为准
        NONE,
        //开启
        TRUE,
        //关闭
        FALSE;
    }
}
