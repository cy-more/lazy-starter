package com.lazy.web.param;

import cn.hutool.core.bean.BeanUtil;
import com.lazy.annotation.YsParamTrans;
import com.lazy.annotation.YsParamTransTrim;
import com.lazy.exception.BizException;
import com.lazy.utils.YsTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ：cy
 * @description：dto和领域对象交互
 * @date ：2022/1/19 9:51
 */
@Slf4j
public class AssemberUtil {

    /**
     * 参数转换
     * 字段：
     * XXStamp(Long) -> XX(LocalDateTime)
     * @param bean
     */
    public static Object transToDomain(Object bean){
        if (bean == null){
            return null;
        }
        //获取属性列表
        Class<?> cls = bean.getClass();
        Field[] fields = cls.getDeclaredFields();
        Map<String, Field> beanMap = Arrays.stream(fields).collect(Collectors.toMap(f -> {
            f.setAccessible(true);
            return f.getName();
        }, Function.identity()));

        Map<String, Object> transValueMap = new HashMap<>();
        try {
            for (Map.Entry<String, Field> entry : beanMap.entrySet()) {
                String key = entry.getKey();
                Field field = entry.getValue();
                Object value = field.get(bean);
                Class<?> type = value == null ? field.getType() : value.getClass();
                //嵌套检测
                if (type.getAnnotation(YsParamTrans.class) != null){
                    transToDomain(value);
                    continue;
                }
                //转换 时间
                if (key.endsWith("Stamp")){
                    String toTransKey = key.substring(0, key.length() - 5);
                    if(value != null
                            && beanMap.containsKey(toTransKey)
                            && beanMap.get(toTransKey).getType().equals(LocalDateTime.class)
                            && beanMap.get(toTransKey).get(bean) == null ) {
                        if (value instanceof Long) {
                            transValueMap.put(toTransKey, YsTimeUtil.getTimeByMilliseconds((Long) value));
                        }else{
                            log.warn("YsParamTrans参数转换不生效,转换时间stamp字段必须为Long，字段名：" + key);
                        }
                    }
                }
                //转换 trim
                if (field.getAnnotation(YsParamTransTrim.class) != null
                        && value != null){
                    if (value instanceof String) {
                        transValueMap.put(key, ((String) value).trim());
                    }else {
                        log.warn("YsParamTransTrim参数转换不生效，转换字段类型必须为String，字段名：" + key);
                    }
                }
            }
            BeanUtil.copyProperties(transValueMap, bean);
        }catch (Exception e){
            throw new BizException("参数转换异常，error:" + e.getMessage());
        }
        return bean;
    }
}
