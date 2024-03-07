package com.lazy.web.config;

import com.alibaba.nacos.common.utils.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义BeanPostProcessor
 *
 * @author zhuquanwen
 * @version 1.0
 * @date 2021/12/29
 * @since jdk11/
 */
@SuppressWarnings({"unused", "unchecked"})
@Slf4j
public class SwaggerBeanPostProcessor implements BeanPostProcessor {
    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        handleWebMvcRequestHandlerProvider(beanName, bean);
        return bean;
    }

    private void handleWebMvcRequestHandlerProvider(String beanName, Object bean) {
        // 处理swagger 在spring boot2.6以上不可用的问题
        boolean modify = (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) &&
                defaultListableBeanFactory.containsBean(beanName);
        if (modify) {
            // 修改属性
            try {
                Field handlerMappingsField = bean.getClass().getDeclaredField("handlerMappings");
                makeAccessible(handlerMappingsField);
                List<RequestMappingInfoHandlerMapping> handlerMappings = (List<RequestMappingInfoHandlerMapping>) handlerMappingsField.get(bean);
                List<RequestMappingInfoHandlerMapping> tmpHandlerMappings = handlerMappings.stream().filter(mapping -> Objects.isNull(mapping.getPatternParser())).collect(Collectors.toList());
                handlerMappings.clear();
                handlerMappings.addAll(tmpHandlerMappings);
            } catch (Exception e) {
                log.warn("修改WebMvcRequestHandlerProvider的属性：handlerMappings出错，可能导致swagger不可用", e);
            }
        }
    }

    public void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
