package com.lazy.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：cy
 * @description ：bean转换
 * @date ：2022/9/22 11:47
 */
public class YsBeanUtil {

    /**
     * 原因：
     * 1.hutool.toBean效率较次
     * 2.hutool有些特殊情况不兼容容易抛异常
     * springAPI缺陷待解决：map转bean不支持
     * @param source
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toBean(Object source, Class<T> clazz) {
        if (null == source) {
            return null;
        }
        if (clazz.isAssignableFrom(source.getClass())){
            return clazz.cast(source);
        }
        if (source instanceof Map || clazz == Map.class){
            return BeanUtil.toBean(source, clazz);
        }
        final T target = ReflectUtil.newInstanceIfPossible(clazz);
        BeanUtils.copyProperties(source, target);

        return target;
    }

    public static <T, R> List<T> toBeanForList(List<? extends R> list, Class<T> clazz){
        if (null == list) {
            return null;
        }
        return list.stream().map(o -> YsBeanUtil.toBean(o, clazz)).collect(Collectors.toList());
    }

}
