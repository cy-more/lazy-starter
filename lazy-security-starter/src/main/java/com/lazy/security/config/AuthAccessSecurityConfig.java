package com.lazy.security.config;

import com.lazy.security.handler.YsAccessDecisionManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * @author ：cy
 * @description：鉴权配置
 * @date ：2022/5/23 15:37
 */
@Configuration
@EnableConfigurationProperties(YsSecurityProperties.class)
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
public class AuthAccessSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final YsSecurityProperties securityProperties;

    public AuthAccessSecurityConfig(YsSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        //权限决策器
        return new YsAccessDecisionManager(securityProperties, super.accessDecisionManager());
    }
}
