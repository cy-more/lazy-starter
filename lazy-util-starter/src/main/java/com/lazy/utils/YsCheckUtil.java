package com.lazy.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author ：cy
 * @description：值验证
 * @date ：2022/6/21 9:49
 */
public class YsCheckUtil {

    /**
     * 物流单号正则
     */
    private final static Pattern LOGISTICS_NO_PATTERN = Pattern.compile("^[A-Za-z0-9]{10,20}$");

    /**
     * 物流单号校验
     * @param logno
     * @return
     */
    public static boolean checkLogisticsNo(String logno){
        return StringUtils.isNotBlank(logno) && LOGISTICS_NO_PATTERN.matcher(logno).matches();
    }
}
