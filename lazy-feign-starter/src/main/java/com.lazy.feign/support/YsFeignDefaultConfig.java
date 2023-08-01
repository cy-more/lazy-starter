package com.lazy.feign.support;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author ：cy
 * @description：默认feign配置
 * @date ：2022/9/6 18:35
 */
@Component
public class YsFeignDefaultConfig {

    @Autowired
    private HttpMessageConverters httpMessageConverters;

    @Bean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    Request.Options feignOptions() {
        return new Request.Options(5, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, true);
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(feignHttpMessageConverter());
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignExceptionDecoder();
    }
//
//    @Bean
//    public Contract contract(){
//        return new YsContract();
//    }

    /**
     * 设置解码器为fastjson
     *
     * @return
     */
    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        return () -> httpMessageConverters;
    }
}
