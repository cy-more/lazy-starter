package com.lazy.security.config;

import com.lazy.security.filter.JwtAuthenticationTokenFilter;
import com.lazy.security.filter.JwtLoginFilter;
import com.lazy.security.handler.YsDeniedHandler;
import com.lazy.security.handler.YsUnAuthHandler;
import com.lazy.security.util.JwtTokenUtil;
import com.lazy.security.util.JwtTokenUtilImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * @author ：cy
 * @description：认证配置
 * @date ：2021/10/15 15:49
 */
@Configuration
@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
@AutoConfigureAfter(CustomizeSecurityConfig.class)
@EnableWebSecurity
@EnableConfigurationProperties(YsSecurityProperties.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final YsSecurityProperties securityProperties;

    public WebSecurityConfig(YsSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * 认证用户的来源
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    /**
     * 配置SpringSecurity相关信息
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        // 不需要保护的资源路径允许访问
        if (securityProperties.getIgnoredUrls() != null) {
            securityProperties.getIgnoredUrls()
                    .forEach(url -> registry.antMatchers(url).permitAll());
        }

        //权限拒绝处理器
        YsDeniedHandler accessDeniedHandler = new YsDeniedHandler();
        //认证失败处理器
        YsUnAuthHandler authenticationEntryPoint = new YsUnAuthHandler();
        //认证
        UsernamePasswordAuthenticationFilter authenticationFilter = jwtLoginFilter();
        //鉴权
        BasicAuthenticationFilter basicAuthenticationFilter = jwtAuthenticationTokenFilter();

        http.headers().cacheControl().disable()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .and()
            .csrf().disable()
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            //认证
            .addFilterAfter(authenticationFilter, BasicAuthenticationFilter.class)
            //禁用session
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .cors()
            .and()
            //异常处理
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler)
            .authenticationEntryPoint(authenticationEntryPoint)
            .and()
            //token状态认证
            .addFilter(basicAuthenticationFilter)
            //响应头自定义
//            .headers(headers -> {
//
//            })
        ;

    }


    @Bean
    @ConditionalOnMissingBean(JwtTokenUtil.class)
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtilImpl(securityProperties);
    }

    /**
     * 获取认证过滤器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(UsernamePasswordAuthenticationFilter.class)
    public UsernamePasswordAuthenticationFilter jwtLoginFilter() throws Exception{
        return new JwtLoginFilter(super.authenticationManager(), jwtTokenUtil());
    }

    /**
     * 获取鉴权过滤器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(BasicAuthenticationFilter.class)
    public BasicAuthenticationFilter jwtAuthenticationTokenFilter() throws Exception{
        JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter(super.authenticationManager());
        jwtAuthenticationTokenFilter.setSecurityProperties(securityProperties);
        jwtAuthenticationTokenFilter.setUserService(userService);
        jwtAuthenticationTokenFilter.setJwtTokenUtil(jwtTokenUtil());
        return jwtAuthenticationTokenFilter;
    }

    /**j
     * 跨域配置
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        return httpServletRequest -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedOrigin("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            config.setMaxAge(3600L);
            return config;
        };
    }
}
