package com.lazy.security.handler;

import com.lazy.exception.ResultCode;
import com.lazy.security.util.YsResponseUtil;
import com.lazy.utils.YsLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：cy
 * @description：未认证
 * @date ：2021/10/27 15:18
 */
@Slf4j
public class YsUnAuthHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse
            , AuthenticationException e) throws IOException, ServletException {
        log.debug(YsLogUtil.initLog(e.getMessage()));
//        if (e instanceof InsufficientAuthenticationException){
//            YsResponseUtil.failHandler(httpServletResponse, ResultCode.UNAUTHORIZED);
//        }
        YsResponseUtil.failHandler(httpServletResponse, ResultCode.UNAUTHORIZED);
    }
}
