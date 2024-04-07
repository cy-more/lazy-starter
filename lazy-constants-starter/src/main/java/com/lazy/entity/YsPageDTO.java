package com.lazy.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Max;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：cy
 * @description：分页dto
 * @date ：2021/9/27 14:24
 */
@ApiModel("分页参数")
@Data
public class YsPageDTO<T extends Serializable>  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Max(10001)
    protected Long size = 10L;

    protected Long current = 1L;

    protected List<YsPageOrderItem> orders = new ArrayList<>();

    /**
     * 命名跟mybatisplus一样就不改了
     */
    protected boolean isSearchCount = true;

    protected T condition;

    /**
     * 限制1w条
     * @param size
     */
    public void setSize(Long size) {
        this.size = size > 10000L ? 10000L : size;
    }

    public void setSizeMax(){
        this.size = 10000L;
    }
}
