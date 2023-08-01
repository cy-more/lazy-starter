package com.lazy.web.security.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.lazy.web.security.constant.AuthConstants;
import com.lazy.web.security.entity.YsLoginDTO;
import com.lazy.web.security.entity.YsUser;
import com.lazy.web.security.util.JwtTokenUtil;
import com.lazy.web.util.YsResponseUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ：cy
 * @description ：登录成功处理器
 * @date ：2022/11/1 17:17
 */
public class YsLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;

    public YsLoginSuccessHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
        //填充 jwt-token信息
        JSONObject tokenContentJson = new JSONObject();
        tokenContentJson.put(AuthConstants.TOKEN_KEY_USERNAME, authResult.getName());
        List<String> currentOrganize = ((YsLoginDTO) authResult.getDetails()).getOrganizes();
        if (ObjectUtil.isNotEmpty(currentOrganize)) {
            tokenContentJson.put(AuthConstants.TOKEN_KEY_ORGANIZE, JSONObject.toJSONString(currentOrganize));
        }
        String tokenContent = tokenContentJson.toJSONString();

        String token = jwtTokenUtil.generateToken(tokenContent);
        String refreshToken = jwtTokenUtil.generateRefreshToken(tokenContent);
        //将Token信息返回给用户
        Map<String, Object> loginMap = BeanUtil.beanToMap(new JwtTokenUtil.RefreshResult(token, refreshToken));
        loginMap.put("detail", ((YsUser)authResult.getPrincipal()).getDetail());
        YsResponseUtil.successHandler(response, loginMap);
    }
}
