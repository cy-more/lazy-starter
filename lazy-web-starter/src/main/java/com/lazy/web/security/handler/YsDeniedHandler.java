package com.lazy.web.security.handler;

import com.lazy.exception.ResultCode;
import com.lazy.web.util.YsResponseUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：cy
 * @description：权限未通过
 * @date ：2021/10/27 15:16
 */
public class YsDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse
            , AccessDeniedException e) throws IOException, ServletException {
        YsResponseUtil.failHandler(httpServletResponse, ResultCode.FORBIDDEN);
    }
}
