package com.lazy.kd100.entity;

import lombok.Data;

/**
 * @author ：cy
 * @description：快递100推送参数
 * @date ：2022/4/14 17:28
 */
@Data
public class Kd100PushDTO {

    /**
     * 签名
     */
    private String sign;

    /**
     * 公共参数
     */
    private String param;

    /**
     * 查询结果
     */
    private String lastResult;
}
