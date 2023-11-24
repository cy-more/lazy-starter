package com.lazy.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;

/**
 * @author: cy
 * @description：权限配置
 * @date: 2021/10/15 15:49
 **/
@RefreshScope
@Data
@ConfigurationProperties(prefix = "ys.security")
public class YsSecurityProperties {

    /**
     * jwt密钥
     *
     */
    private String jwtSecret;

    /**
     * jwt过期时间（单位s）
     */
    private Long jwtExpiration;

    /**
     * refresh jwt过期时间（单位s）
     */
    private Long jwtExpirationForRefresh = 7 * 24 * 60 * 60L;

    /**
     * jwt请求头key
     */
    private String jwtTokenHead = "Authorization";

    /**
     * jwt参数前缀
     */
    private String jwtTokenBearer = "Mac ";

    /**
     * jwt刷新时间（单位s）
     */
    private Integer jwtRefreshExp = 1800;

    /**
     * 接口白名单资源路径（逗号隔开）
     */
    private List<String> ignoredUrls;

    /**
     * IP白名单（逗号隔开）
     */
    private List<String> ignoredIps;

}
