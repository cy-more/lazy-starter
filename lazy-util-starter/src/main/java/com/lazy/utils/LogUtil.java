package com.lazy.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ：cy
 * @description：日志工具
 * @date ：2021/9/26 14:36
 */
@Slf4j
public class LogUtil {

    public static void logError(Throwable e){
        logError(e, e.getMessage());
    }

    public static void logError(Throwable e, String message){
        log.error(initLog(message), e);
    }

    public static String initLog(String message){
        return "warnMessage:" + message;
    }
}
