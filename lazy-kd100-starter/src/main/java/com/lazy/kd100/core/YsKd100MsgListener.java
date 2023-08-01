package com.lazy.kd100.core;


import com.lazy.kd100.common.Kd100DeliveryState;
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
public @interface YsKd100MsgListener {

    /**
     * 推送url-key
     * 同个key全部推送
     * 其中一个失败即视为全部失败等待重推
     * @return
     */
    String key() default "";

    /**
     * 指定状态监听
     * @return
     */
    Kd100DeliveryState state() default Kd100DeliveryState.NONE;
}
