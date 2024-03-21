package com.lazy.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.lazy.exception.BizException;
import com.lazy.security.entity.YsLoginPasswordDTO;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：cy
 * @description：身份验证过滤器
 * token为json串，key:username
 * @date ：2021/10/13 11:22
 */
@Setter
public class JwtLoginPasswordFilter extends AbstractAuthenticationProcessingFilter {

    public JwtLoginPasswordFilter() {
        super("/login/password");
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
        YsLoginPasswordDTO user = null;
        try {
            user = JSONObject.parseObject(request.getInputStream(), YsLoginPasswordDTO.class);
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
