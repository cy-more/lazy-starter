package com.lazy.kd100.support;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：cy
 * @description：快递100 推送接口
 * @date ：2022/4/19 20:47
 */
@Slf4j
@RestController
@RequestMapping(value = "/com/lazy/kd100")
public class Kd100Controller {

    @Autowired
    Kd100ListenerWebContainer kd100ListenerWebContainer;

    /**
     * 快递100推送快递最新信息
     * @param param
     * @return
     */
    @PostMapping(value = "/push/{key}")
    public JSONObject syncLogistic(@PathVariable("key")String key, String param) {
        return kd100ListenerWebContainer.syncLogistic(key, param);
    }
}
