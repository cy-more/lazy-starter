package com.lazy.security.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.lazy.exception.ResultCode;
import com.lazy.security.config.YsSecurityProperties;
import com.lazy.security.constant.AuthConstants;
import com.lazy.security.constant.HttpConstants;
import com.lazy.security.entity.YsUser;
import com.lazy.security.handler.YsAuthenticationToken;
import com.lazy.security.util.JwtTokenUtil;
import com.lazy.security.util.YsRequestUtil;
import com.lazy.security.util.YsResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT访问控制过滤器
 **/
@Slf4j
@Setter
public class JwtAuthenticationTokenFilter extends BasicAuthenticationFilter {

    private UserDetailsService userService;

    private YsSecurityProperties securityProperties;

    private JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public void setSecurityProperties(YsSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        SecurityContextHolder.setStrategyName(AuthConstants.STRATEGY_NAME);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(securityProperties.getJwtTokenHead());
        //没有登录
        if (header == null || !header.startsWith(securityProperties.getJwtTokenBearer())) {
            chain.doFilter(request, response);
            return;
        }
        //解析token
        String token = header.replace(securityProperties.getJwtTokenBearer(),"");
        JwtTokenUtil.TokenInfo tokenContent;
        try {
            tokenContent = jwtTokenUtil.getUserNameFromToken(token);
        }catch (ExpiredJwtException | JSONException e){
            //过期处理
            YsResponseUtil.failHandler(response, ResultCode.UNAUTHORIZED_EXP);
            return;
        }
        String username = tokenContent.getUsername();
        if (username == null){
            YsResponseUtil.failHandler(response, ResultCode.ILLEGAL_JWT_FORMAT);
            return;
        }
        //从token中获取用户信息
        YsUser ysUser = (YsUser) userService.loadUserByUsername(username);
        if (ysUser != null) {
            //存储会话
            YsAuthenticationToken<JwtTokenUtil.TokenInfo> authResult = new YsAuthenticationToken<>
                    (ysUser.getUsername(), null, ysUser.getAuthorities());
            authResult.setDetails(ysUser.getDetail());
            authResult.setSession(tokenContent);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            //记录操作日志
            operateLog(ysUser, request, response, chain);
        }else{
            YsResponseUtil.failHandler(response, ResultCode.ILLEGAL_USER);
        }
    }

    /**
     * 记录操作日志
     * @param user
     * @param request
     */
    private void operateLog(UserDetails user,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        request = YsRequestUtil.transToCacheRequest(request);

        String contentType = request.getContentType();
        String param = null;
        if (StringUtils.isBlank(contentType)){
            param = "无";
        }else if (contentType.contains(HttpConstants.CONTENT_TYPE_FORM)){
            param = request.getParameterMap().toString();
        }else if(contentType.contains(HttpConstants.CONTENT_TYPE_JSON)){
            param = YsRequestUtil.getBodyString(request);
        }
        log.info("auth{user:" + user.getUsername() + ",uri:" + request.getRequestURI() + ",args:" + param + "}");

        chain.doFilter(request, response);
    }
}
