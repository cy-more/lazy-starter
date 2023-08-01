package com.lazy.feign.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.lazy.entity.ResultMsg;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * @author ：cy
 * @description ：组装feign异常
 * @date ：2022/9/8 14:43
 */
@Slf4j
public class FeignExceptionDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            int httpCode = response.status();
            if(HttpStatus.SERVICE_UNAVAILABLE.value() == httpCode){
                log.warn(response.body().toString());
                return new HystrixBadRequestException("服务正在短暂维护，请稍后再试");
            }

            if(response.body() != null){

                //json字符串转对象
                ObjectMapper mapper = new ObjectMapper();
                ResultMsg resultMsg = mapper.readValue(response.body().asInputStream(), ResultMsg.class);

                // 将resultMsg包装成 HystrixBadRequestException，不会触发FeignClient的Fallback策略
                if (null != resultMsg.getCode()) {
                    return new HystrixBadRequestException(resultMsg.getMsg());
                }
            }

        } catch (IOException ex) {
            log.info("组装feign异常失败，非指定异常，error:" + ex);
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }


}
