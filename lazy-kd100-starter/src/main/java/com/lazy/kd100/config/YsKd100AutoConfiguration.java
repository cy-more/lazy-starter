package com.lazy.kd100.config;

import com.lazy.kd100.support.Kd100ApiService;
import com.lazy.kd100.support.Kd100Handler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：cy
 * @description：主要配置
 * @date ：2022/6/13 15:42
 */
@ConditionalOnProperty(prefix = "kd100.api", value = {"key"})
@EnableConfigurationProperties(Kd100ApiProperties.class)
@Configuration
public class YsKd100AutoConfiguration {

    /**
     * 快递100 api-service
     * @param kd100ApiProperties
     * @return
     */
    @Bean
    public Kd100ApiService kd100ApiService(Kd100ApiProperties kd100ApiProperties) {
        Kd100Handler kd100Handler = new Kd100Handler(kd100ApiProperties);
        return new Kd100ApiService(kd100ApiProperties, kd100Handler);
    }
}
