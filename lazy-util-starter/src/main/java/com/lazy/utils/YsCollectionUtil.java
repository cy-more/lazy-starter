package com.lazy.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

/**
 * @Author cy
 * @Date 2023/5/23 14:58
 * @Description: 集合工具
 */
public class YsCollectionUtil {

    /**
     * 去重
     * 注意事项：会破坏原先排序
     * @param collection
     * @param mapper
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R, C extends Collection<T>> Collection<T> unRepeat(C collection, Function<? super T, ? extends R> mapper){
        HashMap<R, T> unRepeatMap = new HashMap<>();
        collection.forEach(t -> {
            R key = mapper.apply(t);
            unRepeatMap.put(key, t);
        });
        return unRepeatMap.values();
    }
}
