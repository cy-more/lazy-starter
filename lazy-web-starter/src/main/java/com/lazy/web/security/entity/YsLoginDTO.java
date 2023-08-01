package com.lazy.web.security.entity;

import lombok.Data;

import java.util.List;

/**
 * @author ：cy
 * @description ：登陆参数
 * @date ：2022/10/10 15:24
 */
@Data
public class YsLoginDTO {

    private String username;
    private String password;
    private List<String> organizes;
}
