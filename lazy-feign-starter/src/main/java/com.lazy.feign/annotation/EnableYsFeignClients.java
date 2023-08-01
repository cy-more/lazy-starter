package com.lazy.feign.annotation;

import com.lazy.feign.support.YsFeignDefaultConfig;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ：cy
 * @description ：继承至EnableFeignClients
 * @date ：2022/11/23 16:49
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableFeignClients
public @interface EnableYsFeignClients {

    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    Class<?>[] defaultConfiguration() default YsFeignDefaultConfig.class;

    Class<?>[] clients() default {};
}
