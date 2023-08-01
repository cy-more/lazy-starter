package com.lazy.kd100.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ：cy
 * @description：物流信息
 * @date ：2022/6/10 18:09
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kd100SaveDTO {

    /**
     * 物流单号
     */
    String logisticsNo;

    /**
     * 当前物流状态
     */
    String state;

    /**
     * 签收时间
     */
    LocalDateTime signTime;
}
