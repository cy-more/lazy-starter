package com.lazy.security.handler;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * @author ：cy
 * @description ：线程会话
 * @date ：2022/9/30 18:44
 */
public class YsAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /**
     * 当前会话组织
     */
    private List<String> organizes;

    public YsAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public YsAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public List<String> getOrganizes() {
        return organizes;
    }

    public void setOrganizes(List<String> organizes) {
        this.organizes = organizes;
    }
}
