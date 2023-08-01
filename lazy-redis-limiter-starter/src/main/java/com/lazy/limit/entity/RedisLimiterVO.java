package com.lazy.limit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：cy
 * @description：取令牌结果
 * @date ：2022/5/11 14:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisLimiterVO {

    /**
     * 是否通过
     */
    private Boolean isAllowed;

    /**
     * 剩余令牌数
     */
    private Long tokensLeft;
}
