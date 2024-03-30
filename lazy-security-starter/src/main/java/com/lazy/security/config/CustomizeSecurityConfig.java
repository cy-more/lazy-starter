package com.lazy.security.config;

import com.lazy.security.entity.YsUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：cy
 * @description：扩展配置
 * @date ：2022/8/19 10:13
 */
@Configuration
@EnableConfigurationProperties(YsSecurityProperties.class)
public class CustomizeSecurityConfig {

    private final YsSecurityProperties securityProperties;

    public CustomizeSecurityConfig(YsSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * 密码管理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        Map<String,PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }
    /**
     * 加载用户信息
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userService() {
        return username -> new YsUser();
    }

}
