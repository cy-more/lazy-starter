package com.lazy.security.handler;

import com.lazy.exception.BizException;
import com.lazy.exception.ResultCode;
import com.lazy.security.util.YsResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：cy
 * @description ：登录失败处理器
 * @date ：2022/11/1 17:19
 */
public class YsLoginFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Throwable cause = exception.getCause();
        if (cause instanceof BizException){
            BizException bizException = (BizException) cause;
            YsResponseUtil.failHandler(response, bizException.getErrorCode(), bizException.getMessage());
        }else {
            YsResponseUtil.failHandler(response, ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
        }
    }
}
