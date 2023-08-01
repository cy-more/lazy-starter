package com.lazy.web.security.controller;

import com.lazy.entity.ResultMsg;
import com.lazy.web.security.util.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：cy
 * @description：权限通用管理
 * @date ：2021/10/27 16:08
 */
@Api(tags = "权限通用管理")
@RestController
@RequestMapping(value = "/auth/common")
public class AuthController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @ApiOperation("刷新token")
    @PostMapping(value = "/refreshToken")
    public ResultMsg<JwtTokenUtil.RefreshResult> refreshToken(String refreshToken){
        return ResultMsg.ok(jwtTokenUtil.refreshHeadToken(refreshToken));
    }

}
