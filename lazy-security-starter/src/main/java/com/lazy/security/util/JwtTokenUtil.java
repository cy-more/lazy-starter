package com.lazy.security.util;


import lombok.Data;

import java.io.Serializable;

/**
 * @author: cy
 * @date: 2021/11/22 下午18:05
 **/
public interface JwtTokenUtil {

    /**
     * 根据用户信息生成token
     */
    String generateToken(TokenInfo username) ;

    /**
     * 根据用户信息生成refresh-token
     */
    String generateRefreshToken(TokenInfo username) ;

    /**
     * @param refreshToken 刷新用的token
     */
    RefreshResult refreshHeadToken(String refreshToken);

    /**
     * 从token中获取登录用户名
     */
    TokenInfo getUserNameFromToken(String token) ;


    @Data
    class RefreshResult implements Serializable {

        private static final long serialVersionUID = 1L;

        String token;
        String refreshToken;

        public RefreshResult(String token, String refreshToken) {
            this.token = token;
            this.refreshToken = refreshToken;
        }
    }

    @Data
    class TokenInfo implements Serializable {

        private String username;
        private String organize;
    }
}
