package com.lazy.utils.support;


import com.lazy.utils.YsClassUtil;

/**
 * @author ：cy
 * @description：封装rowhandler自动转bean
 * @date ：2022/6/8 16:45
 */
public abstract class YsRowHandler<T>{

    private Class<T> beanClass = currentModelClass();

    private Class<T> currentModelClass() {
        return (Class<T>) YsClassUtil.getSuperClassGenericType(getClass(), 0);
    }

    public Class<T> getBeanClass() {
        return beanClass;
    }

    /**
     * 业务自定义处理方法
     * @param sheetIndex
     * @param rowIndex
     * @param bean
     */
    public abstract void handle(int sheetIndex, long rowIndex, T bean);
}
