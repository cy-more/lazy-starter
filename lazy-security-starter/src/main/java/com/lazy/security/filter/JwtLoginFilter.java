package com.lazy.security.filter;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.lazy.exception.BizException;
import com.lazy.security.entity.YsLoginDTO;
import com.lazy.security.entity.YsOrganize;
import com.lazy.security.entity.YsUser;
import com.lazy.security.handler.YsLoginFailHandler;
import com.lazy.security.handler.YsLoginSuccessHandler;
import com.lazy.security.util.JwtTokenUtil;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：cy
 * @description：身份验证过滤器
 * token为json串，key:username
 * @date ：2021/10/13 11:22
 */
@Setter
@NoArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    public JwtLoginFilter(AuthenticationManager authenticationManager
            , JwtTokenUtil tokenUtil) {
        super(authenticationManager);
        setAuthenticationSuccessHandler(new YsLoginSuccessHandler(tokenUtil));
        setAuthenticationFailureHandler(new YsLoginFailHandler());
    }

    /**
     * 验证用户
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        YsLoginDTO user = null;
        try {
            user = JSONObject.parseObject(request.getInputStream(), YsLoginDTO.class);
        } catch (IOException e) {
            throw new BizException("参数格式异常");
        }
        if (user == null || user.getUsername() == null){
            throw new BizException("请输入用户名");
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword());
        authentication.setDetails(user);
        Authentication authResult = getAuthenticationManager().authenticate(authentication);

        return authResult;
    }

}
