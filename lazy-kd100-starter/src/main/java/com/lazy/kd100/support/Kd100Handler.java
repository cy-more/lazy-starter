package com.lazy.kd100.support;

import com.lazy.kd100.common.Kd100Constants;
import com.lazy.kd100.config.Kd100ApiProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ：cy
 * @description：辅助工具类
 * @date ：2022/6/10 17:41
 */
@Slf4j
public class Kd100Handler {

    Kd100ApiProperties kd100ApiProperties;

    public Kd100Handler(Kd100ApiProperties kd100ApiProperties) {
        this.kd100ApiProperties = kd100ApiProperties;
    }

    /**
     * 获取整个推送路径
     * @param key
     * @return
     */
    public String getPushURI(String key){
        return kd100ApiProperties.getPushIp() + Kd100Constants.PUSH_URL_POSITION + key;
    }

}
