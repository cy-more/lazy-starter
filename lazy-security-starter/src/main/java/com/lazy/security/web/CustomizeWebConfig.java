package com.lazy.security.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：cy
 * @description：扩展配置
 * @date ：2022/8/19 10:13
 */
@Configuration
public class CustomizeWebConfig {

    /**
     * 避免流获取导致的参数丢失问题
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "ParamFilter")
    public ParamFilter ParamFilter() {
        return new ParamFilter();
    }

}
