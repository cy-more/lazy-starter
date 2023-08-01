package com.lazy.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：cy
 * @description：分页排序 字段
 * @date ：2021/9/27 14:28
 */
@Data
public class YsPageOrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String column;
    private boolean asc = true;
}
