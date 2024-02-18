package com.lazy.utils;

import java.util.*;
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


    /**
     * 只包含Map和List的集合 获取最下级kv
     * @param document
     */
    public static List<Map.Entry<String, Object>> extractLeafEntries(Map<String, Object> document){
        List<Map.Entry<String, Object>> leafEntries = new ArrayList<>();
        extractLeafEntries(document, leafEntries);
        return leafEntries;
    }

    /**
     * 只包含Map和List的集合 获取最下级kv
     * @param document
     * @param leafEntries 结果集合
     */
    private static void extractLeafEntries(Map<String, Object> document, List<Map.Entry<String, Object>> leafEntries) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            if (entry.getValue() instanceof Map) {
                extractLeafEntries((Map) entry.getValue(), leafEntries);
            } else if (entry.getValue() instanceof List) {
                List<?> list = (List<?>) entry.getValue();
                for (Object item : list) {
                    if (item instanceof Map) {
                        extractLeafEntries((Map) item, leafEntries);
                    }
                }
            } else {
                leafEntries.add(entry);
            }
        }
    }

    /**
     * 只包含Map和List的集合 获取所有key
     * @param document
     * @return
     */
    public static List<String> getAllKeys(Map<String, Object> document) {
        List<String> keys = new ArrayList<>();
        extractKeys(document, keys);
        return keys;
    }

    private static void extractKeys(Map<String, Object> document, List<String> keys) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            keys.add(entry.getKey());
            Object value = entry.getValue();

            // 如果值是另一个Document，递归地调用extractKeys
            if (value instanceof Map) {
                extractKeys((Map<String, Object>) value, keys);
            }
            // 如果值是List类型，遍历List中的每个元素
            else if (value instanceof List<?>) {
                for (Object item : (List<?>) value) {
                    if (item instanceof Map) {
                        extractKeys((Map<String, Object>) item, keys);
                    }
                }
            }
        }
    }

    /**
     * 只包含Map和List的集合 获取所有value
     * @param document
     * @return
     */
    public static List<Object> getAllValues(Map<String, Object> document) {
        List<Object> values = new ArrayList<>();
        extractValues(document, values);
        return values;
    }

    private static void extractValues(Map<String, Object> document, List<Object> values) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            Object value = entry.getValue();

            // 如果值是另一个Document，递归地调用extractValues
            if (value instanceof Map) {
                extractValues((Map<String, Object>) value, values);
            }
            // 如果值是List类型，遍历List中的每个元素
            else if (value instanceof List<?>) {
                for (Object item : (List<?>) value) {
                    if (item instanceof Map) {
                        extractValues((Map<String, Object>) item, values);
                    } else {
                        values.add(item);
                    }
                }
            } else {
                values.add(value);
            }
        }
    }


    /**
     * 只包含Map和List的集合 根据path提取值
     * @param document
     * @param path
     * @return
     */
    public static List<Object> extractValuesByPath(Map<String, Object> document, String path) {
        List<Object> values = new ArrayList<>();
        extractValuesRecursive(document, path.split("\\."), 0, values);
        return values;
    }

    private static void extractValuesRecursive(Object current, String[] path, int index, List<Object> values) {
        if (index > path.length || current == null) {
            return;
        }
        if (index == path.length) {
            values.add(current);
            return;
        }

        String key = path[index];
        if (current instanceof Map) {
            Map<String, Object> currentDoc = (Map<String, Object>) current;
            Object next = currentDoc.get(key);
            extractValuesRecursive(next, path, index + 1, values);
        } else if (current instanceof List) {
            List<?> currentList = (List<?>) current;
            for (Object item : currentList) {
                extractValuesRecursive(item, path, index, values);
            }
        }
    }
}
