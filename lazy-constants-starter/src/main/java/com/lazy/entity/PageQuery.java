package com.lazy.entity;

import lombok.Data;

/**
 * @author ：cy
 * @description：分页
 * @date ：2021/9/30 18:02
 */
@Data
public class PageQuery {

    protected long current = 1;

    protected long size = 10;

    public PageQuery(long current, long size) {
        this.size = size;
        this.current = size * (current - 1);
    }

    public PageQuery(YsPageDTO<?> dto){
        this.size = dto.getSize();
        this.current = size * (dto.getCurrent() - 1);
    }
}
