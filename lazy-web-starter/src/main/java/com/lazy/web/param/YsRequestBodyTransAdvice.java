package com.lazy.web.param;

import com.lazy.annotation.YsParamTrans;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

/**
 * @author ：cy
 * @description ：参数转换
 * @date ：2022/11/2 18:10
 */
@Slf4j
@ControllerAdvice
public class YsRequestBodyTransAdvice extends RequestBodyAdviceAdapter {
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        return methodParameter.hasParameterAnnotation(YsParamTrans.class)
                || methodParameter.getParameterType().getAnnotation(YsParamTrans.class) != null;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AssemberUtil.transToDomain(body);
    }

}
