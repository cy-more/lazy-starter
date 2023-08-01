package com.lazy.web.security.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lazy.exception.ResultCode;
import com.lazy.web.constant.HttpConstants;
import com.lazy.web.security.config.YsSecurityProperties;
import com.lazy.web.security.constant.AuthConstants;
import com.lazy.web.security.entity.YsUser;
import com.lazy.web.security.handler.YsAuthenticationToken;
import com.lazy.web.security.util.JwtTokenUtil;
import com.lazy.web.util.YsRequestUtil;
import com.lazy.web.util.YsResponseUtil;
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
        JSONObject tokenContent;
        try {
            tokenContent = JSONObject.parseObject(jwtTokenUtil.getUserNameFromToken(token));
        }catch (ExpiredJwtException | JSONException e){
            //过期处理
            YsResponseUtil.failHandler(response, ResultCode.UNAUTHORIZED_EXP);
            return;
        }
        String username = tokenContent.getString(AuthConstants.TOKEN_KEY_USERNAME);
        String organizeJson = tokenContent.getString(AuthConstants.TOKEN_KEY_ORGANIZE);
        if (username == null){
            YsResponseUtil.failHandler(response, ResultCode.ILLEGAL_JWT_FORMAT);
            return;
        }
        //从token中获取用户信息
        YsUser ysUser = (YsUser) userService.loadUserByUsername(username);
        if (ysUser != null) {
            //存储会话
            YsAuthenticationToken authResult = new YsAuthenticationToken
                    (ysUser.getUsername(), null, ysUser.getAuthorities());
            authResult.setDetails(ysUser.getDetail());
            authResult.setOrganizes(JSONArray.parseArray(organizeJson, String.class));
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
