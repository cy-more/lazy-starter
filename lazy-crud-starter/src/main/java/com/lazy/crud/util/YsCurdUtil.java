package com.lazy.crud.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lazy.entity.YsPageDTO;


/**
 * @author: cy
 * @description:
 * @date: 2023-10-26 18:27
 **/
public class YsCurdUtil {

    public static <T> Page<T> toPage(YsPageDTO<? extends T> dto) {
        return new Page<>(dto.getCurrent(), dto.getSize());
    }

}
