package com.lazy.web.param;

import com.lazy.annotation.YsParamTrans;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

/**
 * @author ：cy
 * @description ：attribute参数转换
 * @date ：2022/11/3 11:02
 */
public class YsAttributeParamProcessor extends ServletModelAttributeMethodProcessor {
    /**
     * Class constructor.
     *
     * @param annotationNotRequired if "true", non-simple method arguments and
     *                              return values are considered model attributes with or without a
     *                              {@code @ModelAttribute} annotation
     */
    public YsAttributeParamProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(YsParamTrans.class);
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        super.bindRequestParameters(binder, request);
        AssemberUtil.transToDomain(binder.getTarget());
    }
}
