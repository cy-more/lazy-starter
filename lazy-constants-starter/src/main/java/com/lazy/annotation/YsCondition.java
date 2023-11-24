package com.lazy.annotation;


import java.lang.annotation.*;

/**
 * @author ：cy
 * @description：查询条件注解
 * 配合YsServerImpl.getConditionWapper
 * @date ：2021/12/20 15:16
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface YsCondition {

    /**
     * 条件类型
     * @return
     */
    Type type() default Type.EQ;

    /**
     * 条件对应数据库字段，无则默认以mybatis映射转换
     * @return
     */
    String column() default "";

    /**
     * 是否忽略为空(包含空字符，空数组）的值，默认是(defaultValue有值不会生效)
     * @return
     */
    boolean ignoreNullValue() default true;

    /**
     * 默认值,空字符不生效
     * @return
     */
    String defaultValue() default "";

    /**
     * 排序，暂不支持多字段
     * @return
     */
    OrderType orderType() default OrderType.IGNORE;


    enum Type {

        /**
         * 表示该字段忽略
         */
        IGNORE,

        /**
         * like
         */
        LIKE,

        /**
         * in
         */
        IN,

        /**
         * 大于等于
         */
        GE,

        /**
         * 小于等于
         */
        LE,

        /**
         * 大于
         */
        GT,

        /**
         * 小于
         */
        LT,

        /**
         * 等于
         */
        EQ,

        /**
         * 是否为空
         * true(is null)
         * false(is not null)
         * 支持类型：boolean/string
         */
        ISNULL
    }

    enum OrderType{

        /**
         * 不做排序
         */
        IGNORE,

        /**
         * 正序
         */
        ASC,

        /**
         * 降序
         */
        DESC
    }
}
