package com.lazy.kd100.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lazy.kd100.common.Kd100DeliveryState;
import com.lazy.kd100.entity.Kd100PushDTO;
import com.lazy.kd100.entity.Kd100SaveDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ：cy
 * @description：kd100相关工具类
 * @date ：2022/6/13 11:17
 */
@Slf4j
public class YsKd100Util {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Kd100PushDTO转Kd100SaveDTO
     * @param pushDTO
     * @return
     */
    public static Kd100SaveDTO transSave(Kd100PushDTO pushDTO){
        Kd100SaveDTO saveDTO = new Kd100SaveDTO();
        JSONObject lastResult = JSONObject.parseObject(pushDTO.getLastResult());
        //物流单号
        saveDTO.setLogisticsNo(lastResult.getString("nu"));
        //物流状态
        saveDTO.setState(lastResult.getString("state"));

        JSONArray dataList = lastResult.getJSONArray("data");
        if (ObjectUtil.isEmpty(dataList)){
            JSONObject data = dataList.getJSONObject(0);
            if (Kd100DeliveryState.SIGN.getCode().equals(lastResult.getString("state"))){
                //签收时间
                saveDTO.setSignTime(LocalDateTime.parse(data.getString("ftime"), FORMATTER));
            }
        }

        return saveDTO;
    }


    /**
     * 推送获取物流单号
     * @param lastResult
     * @return
     */
    public static String getLogisticsNo(JSONObject lastResult){
        if (ObjectUtil.isEmpty(lastResult)){
            return null;
        }
        String logisticsNo = lastResult.getString("nu");
        if (StringUtils.isBlank(logisticsNo)){
            warnGetPushInfo("物流单号", lastResult.toJSONString());
        }
        return logisticsNo;
    }

    /**
     * 推送获取物流状态
     * @param lastResult
     * @return
     */
    public static String getState(JSONObject lastResult){
        if (ObjectUtil.isEmpty(lastResult)){
            return null;
        }
        String state = lastResult.getString("state");
        if (ObjectUtil.isEmpty(state)){
            warnGetPushInfo("物流状态", lastResult.toJSONString());
        }
        return state;
    }

    public static JSONObject getLastResult(Kd100PushDTO dto){
        JSONObject lastResult = JSONObject.parseObject(dto.getLastResult());
        if (ObjectUtil.isEmpty(lastResult)){
            warnGetPushInfo("消息内容lastResult", dto.getLastResult());
        }
        return lastResult;
    }

    /**
     * 格式变更告警
     * @param keyName
     */
    private static void warnGetPushInfo(String keyName, String content){
        log.warn(String.format("推送报文中获取不到%s，请检查报表格式是否有变更,报文内容：%s", keyName, content));
    }

    private YsKd100Util() {
    }
}
