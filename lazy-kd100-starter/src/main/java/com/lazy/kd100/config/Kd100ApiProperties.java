package com.lazy.kd100.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ：cy
 * @description：快递100-api配置
 * @date ：2022/4/13 10:14
 */
@Component
@Data
@ConfigurationProperties(prefix = "kd100.api")
public class Kd100ApiProperties {

    /**
     * 使用api-service必须配置的4项
     */
    private String key;
    private String customer;
    private String secret;
    private String userId;
//    private String siId;
//    private String tid;
//    private String secretKey;
//    private String secretSecret;

    /**
     * 验签key
     */
    private String saltKey;

    /**
     * 推送时，使用key订阅和推送监听时需要配置
     */
    private String pushIp;
}
