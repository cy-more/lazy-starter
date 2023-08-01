package com.lazy.annotation;


import java.lang.annotation.*;

/**
 * @author ：cy
 * @description ：参数转换
 * String -> String.trim()
 * @date ：2022/11/2 18:10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YsParamTransTrim {
}
