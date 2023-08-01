package com.lazy.kd100.core;

import com.lazy.kd100.entity.Kd100PushDTO;

/**
 * @author ：cy
 * @description：监听类
 * @date ：2022/6/10 11:46
 */
public interface YsKd100Listener {

    void handle(Kd100PushDTO kd100PushDTO);
}
