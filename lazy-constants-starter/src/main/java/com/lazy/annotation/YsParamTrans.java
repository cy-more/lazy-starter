package com.lazy.annotation;


import java.lang.annotation.*;

/**
 * @author ：cy
 * @description ：参数转换
 * 在方法或类备注只要有一个都会生效
 * 嵌套类要生效必须在类上配置注解
 *
 * 默认转换：
 * XXStamp(Long) -> XX(LocalDateTime)
 * 扩展注解转换：
 * String -> String.trim() -----@YsParamTransTrim
 * @date ：2022/11/2 18:10
 */
@Target({ElementType.PARAMETER,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YsParamTrans {
}
