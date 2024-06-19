package com.lazy.security.handler;

import com.lazy.exception.BizException;
import com.lazy.exception.ResultCode;
import com.lazy.security.entity.YsAuthBizException;
import com.lazy.security.util.YsResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @date: 2024-05-20 0:47
 * @description: TODO
 * @author: cy
 */
public class YsLoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof YsAuthBizException){
            YsAuthBizException authBizException = (YsAuthBizException) exception;
            YsResponseUtil.failHandler(response, authBizException.getErrorCode(), authBizException.getMessage());
        }else {
            YsResponseUtil.failHandler(response, ResultCode.UNAUTHORIZED.getCode(), "认证不通过");
        }
    }
}
