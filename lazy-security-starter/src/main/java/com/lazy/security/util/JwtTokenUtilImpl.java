package com.lazy.security.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.lazy.security.config.YsSecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: cy
 * @date: 2021/11/22 下午18:05
 **/
public class JwtTokenUtilImpl implements JwtTokenUtil{

    private static final String CLAIM_KEY_USERNAME = "sub";

    private static final String CLAIM_KEY_CREATED = "created";

    private static final String CLAIM_KEY_EXP = "exp";

    private final YsSecurityProperties securityProperties;

    public JwtTokenUtilImpl(YsSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }


    /**
     * 根据用户信息生成token
     */
    @Override
    public String generateToken(TokenInfo tokenInfo) {
        Map<String, Object> claims = new HashMap<>(3);
        claims.put(CLAIM_KEY_USERNAME, JSONObject.toJSONString(tokenInfo));
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims, securityProperties.getJwtExpiration());
    }

    /**
     * 根据用户信息生成refresh-token
     */
    @Override
    public String generateRefreshToken(TokenInfo tokenInfo) {
        Map<String, Object> claims = new HashMap<>(3);
        claims.put(CLAIM_KEY_USERNAME, JSONObject.toJSONString(tokenInfo));
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims, securityProperties.getJwtExpirationForRefresh());
    }

    /**
     * @param refreshToken 刷新用的token
     */
    public RefreshResult refreshHeadToken(String refreshToken) {
        if (StrUtil.isEmpty(refreshToken)) {
            return null;
        }
//        String token = refreshToken.substring(securityProperties.getJwtTokenHead().length());
        String token = refreshToken;
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        //token校验不通过
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        //如果token已经过期，不支持刷新
        if (isTokenExpired(token)) {
            return null;
        }
        //如果token在30分钟之内刚刷新过，返回原token
//        if (tokenRefreshJustBefore(token, securityProperties.getJwtRefreshExp())) {
//            return null;
//        }
        claims.put(CLAIM_KEY_CREATED, new Date());
        return new RefreshResult(generateToken(claims, securityProperties.getJwtExpiration())
                                , generateToken(claims, securityProperties.getJwtExpirationForRefresh()));
    }

    /**
     * 从token中获取登录用户名
     */
    public TokenInfo getUserNameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Date expiredDate = claims.getExpiration();
        if (isExpired(expiredDate)) {
            throw new IllegalArgumentException("JWT已过期");
        }
        return JSONObject.parseObject(claims.getSubject(), TokenInfo.class);
    }

    /**
     * 根据负责生成JWT的token
     */
    private String generateToken(Map<String, Object> claims, Long expTime) {
        //生成token的过期时间
        Date expDate = new Date(System.currentTimeMillis() + expTime * 1000);
        long exp = expDate.getTime();
        claims.put(CLAIM_KEY_EXP, exp);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expDate)
                .signWith(SignatureAlgorithm.HS512, securityProperties.getJwtSecret())
                .compact();
    }

    /**
     * 从token中获取JWT中的负载
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(securityProperties.getJwtSecret())
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * 判断token是否已经失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return isExpired(expiredDate);
    }

    private boolean isExpired(Date expiredDate) {
        return System.currentTimeMillis() > expiredDate.getTime();
    }

    /**
     * 从token中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 判断token在指定时间内是否刚刚刷新过
     *
     * @param token 原token
     * @param time  指定时间（秒）
     */
    private boolean tokenRefreshJustBefore(String token, int time) {
        Claims claims = getClaimsFromToken(token);
        Date created = claims.get(CLAIM_KEY_CREATED, Date.class);
        Date refreshDate = new Date();
        // 刷新时间在创建时间的指定时间内
        return refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time));
    }


//    public static void main(String[] args) {
//        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil(new YsSecurityProperties());
//        System.out.println(jwtTokenUtil.generateToken("cy"));
//    }
}
