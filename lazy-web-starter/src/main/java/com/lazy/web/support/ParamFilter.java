package com.lazy.web.support;

import com.lazy.web.util.YsRequestUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author ：cy
 * @description：参数过滤器
 * @date ：2022/3/3 17:38
 */
@Order(1)
@Component
public class ParamFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            servletRequest = YsRequestUtil.transToCacheRequest((HttpServletRequest) servletRequest);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
