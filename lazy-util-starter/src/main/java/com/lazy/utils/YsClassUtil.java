package com.lazy.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.messaging.MappingFastJsonMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.support.MessageBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author ：cy
 * @description：类工具
 * @date ：2021/11/19 10:38
 */
@Slf4j
public class YsClassUtil {

    public final static MappingFastJsonMessageConverter JSON_CONVERTER = new MappingFastJsonMessageConverter();

    /**
     * <p>
     * 反射对象获取泛型
     * </p>
     * copy from com.baomidou.mybatisplus.core.toolkit.ReflectionKit
     * @param clazz 对象
     * @param index 泛型所在位置
     * @return Class
     */
    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            log.warn(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            log.warn(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index,
                    clazz.getSimpleName(), params.length));
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            log.warn(String.format("Warn: %s not set the actual class on superclass generic parameter",
                    clazz.getSimpleName()));
            return Object.class;
        }
        return (Class<?>) params[index];
    }

    /**
     * 对象转byte[]
     * copy from rocketmqUtil.convertToRocketMessage
     * ps: string转换编码默认utf-8
     *      序列化：alibaba.fastjson
     * @param obj
     * @return
     */
    public static byte[] toByte(Object obj){
        byte[] objByte;
        try {
            if (null == obj) {
                throw new RuntimeException("the object cannot be empty");
            }
            if (obj instanceof String) {
                objByte = ((String) obj).getBytes(StandardCharsets.UTF_8);
            } else if (obj instanceof byte[]) {
                objByte = (byte[]) obj;
            } else {
                String jsonObj = JSONObject.toJSONString(obj);
                if (StringUtils.isBlank(jsonObj)) {
                    throw new RuntimeException(String.format(
                            "empty after conversion [payloadClass:%s,payloadObj:%s]", obj.getClass(), obj));
                }
                objByte = jsonObj.getBytes(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("convert to byte[] failed.", e);
        }
        return objByte;
    }

    /**
     * byte转对象
     * ps:
     * copy from DefaultRocketMQListenerContainer.doConvertMessage
     * 目前只支持alibaba.fastjson转换的byte[]
     * 待添加：
     * byteArrayMessageConverter
     * StringMessageConverter
     * 泛型支持
     * @param objByte
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObjectByByte(byte[] objByte, Class<T> clazz){
        if (null == objByte || null == clazz) {
            throw new RuntimeException("byte[] & clazz cannot be empty");
        }
        String str = new String(objByte, StandardCharsets.UTF_8);
        if (Objects.equals(clazz, String.class)) {
            return (T)str;
        } else {
            // If msgType not string, use ali.json change it.
            try {
                //if the clazz has not Generic Parameter
                return (T) JSON_CONVERTER.fromMessage(MessageBuilder.withPayload(str).build(), clazz);
            } catch (Exception e) {
                log.info("convert failed. str:{}, class:{}", str, clazz);
                throw new RuntimeException("cannot convert byte[] to " + clazz, e);
            }
        }
    }
}
