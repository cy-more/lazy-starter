package com.lazy.web.security.handler;

import com.lazy.web.security.config.YsSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author ：cy
 * @description：权限决策
 * from AffirmativeBased
 * @date ：2021/11/22 13:45
 */
@Slf4j
public class YsAccessDecisionManager implements AccessDecisionManager {

    private final YsSecurityProperties securityProperties;

    /**
     * 被装饰决策类
     */
    private final AccessDecisionManager accessDecisionManager;

    public YsAccessDecisionManager(YsSecurityProperties securityProperties, AccessDecisionManager accessDecisionManager) {
        this.securityProperties = securityProperties;
        this.accessDecisionManager = accessDecisionManager;
    }

    @Override
    public void decide(Authentication authentication, Object object,
                       Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        // 当接口未被配置资源时直接放行
        if (CollectionUtils.isEmpty(configAttributes)) {
            return;
        }
        //跳过鉴权通道(开发环境通道)
        if (securityProperties.getIgnoredUrls().contains("/**")){
            return;
        }

        accessDecisionManager.decide(authentication, object, configAttributes);

    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return accessDecisionManager.supports(configAttribute);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return accessDecisionManager.supports(aClass);
    }

}
