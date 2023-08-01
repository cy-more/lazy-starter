package com.lazy.kd100.support;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.lazy.kd100.common.Kd100DeliveryState;
import com.lazy.kd100.core.YsKd100Listener;
import com.lazy.kd100.core.YsKd100MsgListener;
import com.lazy.kd100.entity.Kd100PushDTO;
import com.lazy.kd100.util.YsKd100Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：cy
 * @description：推送监听容器
 * @date ：2022/6/10 11:27
 */
@Slf4j
@Component
public class Kd100ListenerWebContainer {

    private final Map<String, List<YsKd100Listener>> listenerMap = new HashMap<>();

    /**
     * 订阅推送监听者
     * @param key
     * @param ysKd100Listener
     */
    public void pollListener(String key, Kd100DeliveryState state, YsKd100Listener ysKd100Listener){
        List<YsKd100Listener> listenerList = listenerMap.getOrDefault(key, new ArrayList<>(1));
        listenerList.add(ysKd100Listener);
        listenerMap.put(key, listenerList);
    }

    /**
     * 推送事件处理
     * @param key
     * @param param
     * @return
     */
    public JSONObject syncLogistic(String key, String param){
        log.info("快递100推送：" + param);
        JSONObject jsonParam = JSONObject.parseObject(param);
        Kd100PushDTO kd100PushDTO = new Kd100PushDTO();
        kd100PushDTO.setLastResult(jsonParam.getString("lastResult"));

//        //验签
//        kd100ApiService.checkSign(kd100PushDTO.getSign(), kd100PushDTO.getParam(), logisticsNo);
        try {
            //业务
            List<YsKd100Listener> ysKd100Listeners = listenerMap.get(key);
            if (ObjectUtil.isEmpty(ysKd100Listeners)){
//                log.error("warnMessage: 找不到推送的监听处理器，请检查订阅ip和key是否正确，key:" + key);
                return resultMsg();
            }
            ysKd100Listeners.forEach(listener -> {
                Kd100DeliveryState listenState = listener.getClass().getAnnotation(YsKd100MsgListener.class).state();
                //不指定state
                if (Kd100DeliveryState.NONE == listenState) {
                    listener.handle(kd100PushDTO);
                    return;
                }
                //指定state
                String state = YsKd100Util.getState(YsKd100Util.getLastResult(kd100PushDTO));
                if (listenState.getCode().equals(state)){
                    listener.handle(kd100PushDTO);
                }
            });
        }catch (Exception e){
            log.error("快递100推送异常:" + e.getMessage(), e);
            return resultMsgFail();
        }

        return resultMsg();
    }

    /**
     * 返回失败
     * @return
     */
    private JSONObject resultMsgFail(){
        JSONObject result = new JSONObject();
        result.put("result", false);
        result.put("returnCode ", "500");
        result.put("message", "失败");
        return result;
    }

    /**
     * 返回成功
     * @return
     */
    private JSONObject resultMsg(){
        JSONObject result = new JSONObject();
        result.put("result", true);
        result.put("returnCode ", "200");
        result.put("message", "成功");
        return result;
    }
}
