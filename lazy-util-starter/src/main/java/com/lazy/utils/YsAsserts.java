package com.lazy.utils;

import cn.hutool.core.util.ObjectUtil;
import com.lazy.exception.BizException;

/**
 * @author ：cy
 * @description：断言
 * @date ：2021/10/26 18:23
 */
public class YsAsserts {

    /**
     * 判空
     * @param source
     * @param message
     */
    public static void isNull(Object source, String message){
        if (ObjectUtil.isEmpty(source)){
            throw new BizException(message);
        }
    }

    /**
     * 判不能为空
     * @param source
     * @param message
     */
    public static void notNull(Object source, String message){
        if (!ObjectUtil.isEmpty(source)){
            throw new BizException(message);
        }
    }

    /**
     * 判断结果断言
     * @param flag
     * @param message
     */
    public static void isTrue(boolean flag, String message){
        if (flag){
            throw new BizException(message);
        }
    }
    public static void isFalse(boolean flag, String message){
        if (!flag){
            throw new BizException(message);
        }
    }
}
