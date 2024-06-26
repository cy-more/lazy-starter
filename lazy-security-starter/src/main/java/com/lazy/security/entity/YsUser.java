package com.lazy.security.entity;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：cy
 * @description：通用权限user
 * @date ：2021/10/13 11:10
 */
@Data
@Component
public class YsUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private Collection<GrantedAuthority> authorities;
    private Collection<String> roleIds;
    private boolean isEnable = true;
    private Object detail;
    private List<YsOrganize> organizes;

    public void setAuthorities(Collection<String> authorities) {
        this.authorities = authorities.stream().map(YsAuthority::new).collect(Collectors.toList());
    }

    public void setOrganizes(Collection<String> organizes){
        if (ObjectUtil.isEmpty(organizes)){
            return;
        }
        this.organizes = organizes.stream().map(YsOrganize::new).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 密码过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 启用/禁用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return isEnable;
    }

}
