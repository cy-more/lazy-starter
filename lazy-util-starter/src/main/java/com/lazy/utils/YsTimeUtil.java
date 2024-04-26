package com.lazy.utils;

import cn.hutool.core.date.DatePattern;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author ：cy
 * @description：时间工具
 * @date ：2021/11/2 15:29
 */
public class YsTimeUtil {

    /**
     * 毫秒时间戳转时间
     * @param milliseconds
     * @return
     */
    public static LocalDateTime getTimeByMilliseconds(Long milliseconds){
        if (milliseconds == null){
            return null;
        }
        return LocalDateTime.ofEpochSecond(milliseconds/1000, 0, ZoneOffset.ofHours(8));
    }

    /**
     * 毫秒时间戳转时间
     * @param time
     * @return
     */
    public static Long getMillisecondsByTime(LocalDateTime time){
        if (time == null){
            return null;
        }
        return time.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * LocalDateTime转String
     * @param time
     * @param pattern
     * @return
     */
    public static String getStringByTime(TemporalAccessor time, String pattern){
        return DateTimeFormatter.ofPattern(pattern).format(time);
    }

    /**
     * LocalDateTime转String
     * 默认 yyyy-MM-dd HH:mm:ss
     * @param time
     * @return
     */
    public static String getStringByTime(TemporalAccessor time){
        return getStringByTime(time, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * String转LocalDateTime
     * @param time
     * @param pattern
     * @return
     */
    public static LocalDateTime getTimeByString(String time, String pattern){
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(pattern));
    }
}
