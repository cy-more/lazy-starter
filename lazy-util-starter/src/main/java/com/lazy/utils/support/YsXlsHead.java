package com.lazy.utils.support;


import java.lang.annotation.*;

/**
 * @author ：cy
 * @description：Xls 标题
 * @date ：2021/9/26 13:53
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface YsXlsHead {

    /**
     * 名称
     * @return name
     */
    String value() default "";

    /**
     * 导入时，是否验空
     * @return
     */
    boolean isCheck() default false;
}
