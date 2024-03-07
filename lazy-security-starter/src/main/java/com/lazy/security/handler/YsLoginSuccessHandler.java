package com.lazy.security.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.lazy.security.constant.AuthConstants;
import com.lazy.security.entity.YsLoginDTO;
import com.lazy.security.entity.YsUser;
import com.lazy.security.util.JwtTokenUtil;
import com.lazy.security.util.YsResponseUtil;
import com.lazy.utils.YsBeanUtil;
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
        JwtTokenUtil.TokenInfo<Object> tokenContentJson = new JwtTokenUtil.TokenInfo<>();
        tokenContentJson.setUsername(authResult.getName());

        String token = jwtTokenUtil.generateToken(tokenContentJson);
        String refreshToken = jwtTokenUtil.generateRefreshToken(tokenContentJson);
        //将Token信息返回给用户
        Map<String, Object> loginMap = YsBeanUtil.toBean(new JwtTokenUtil.RefreshResult(token, refreshToken), Map.class);
        loginMap.put("detail", ((YsUser)authResult.getPrincipal()).getDetail());
        YsResponseUtil.successHandler(response, loginMap);
    }
}
