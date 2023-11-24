package com.lazy.security.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author ：cy
 * @description：权限
 * @date ：2021/11/1 18:13
 */
public class YsAuthority implements GrantedAuthority {

    String authority;

    public YsAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
