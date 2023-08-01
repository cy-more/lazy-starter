package com.lazy.utils;

import com.lazy.exception.BizException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：cy
 * @description：通用工具
 * @date ：2021/9/18 15:00
 */
public class NoUtil {
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SCALE = 62;
    private static final int MIN_LENGTH = 5;
    /**
     * 驼峰判断
     */
    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
    /**
     * 只保留数字
     */
    private static final Pattern ONLY_NUMBER_PATTERN = Pattern.compile("[^0-9]");

    private static final SecureRandom random = new SecureRandom();

    /**
     * 数字转62进制
     *
     * @param num
     * @return
     */
    public static String hex10ToHex62(long num) {
        StringBuilder sb = new StringBuilder();
        int remainder;
        while (num > SCALE - 1) {
            //对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转字符串
            remainder = (int) (num % SCALE);
            sb.append(CHARS.charAt(remainder));
            //除以进制数，获取下一个末尾数
            num = num / SCALE;
        }
        sb.append(CHARS.charAt((int) num));
        String value = sb.reverse().toString();
        return StringUtils.leftPad(value, MIN_LENGTH, '0');
    }

    /**
     * 获取随机数
     * @param m
     * @param n
     * @return
     */
    public static Integer getRandomInt(int m, int n) {
        return m + random.nextInt(n - m);
    }

    /**
     * 0前补位
     *
     * @param str        目标
     * @param targetSize 目标长度
     * @param isPrefix   true:前补位-例：000X false:后补位-例：X000
     * @return
     */
    public static String suppleStrByZero(String str, Integer targetSize, boolean isPrefix) {
        int gapSize = targetSize - str.length();
        if (gapSize == 0) {
            return str;
        } else if (gapSize < 0) {
            throw new BizException("targetSize is smaller");
        }

        StringBuilder result = new StringBuilder(targetSize);
        for (int i = 0; i < gapSize; i++) {
            result.append('0');
        }
        if (isPrefix) {
            return result.append(str).toString();
        } else {
            return str + result;
        }
    }


    /**
     * "*"匹配 （暂定）
     *
     * @param regex
     * @param input
     * @return
     */
    public static boolean matcherForAsterisk(String regex, String input) {
        String regexForAsterisk = regex.replace("**", "*").replace("*", ".*");
        return Pattern.matches(regexForAsterisk, input);
    }

    /**
     * 驼峰转下划线
     *
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 转bigDecimal
     * @param value
     * @return
     */
    public static BigDecimal toBigDecimal(Object value) {
        BigDecimal bigDec = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                bigDec = (BigDecimal) value;
            } else if (value instanceof String) {
                bigDec = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                bigDec = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                bigDec = BigDecimal.valueOf(((Number) value).doubleValue());
            } else {
                throw new BizException("类型不正确无法转为bigdecimal，class:" + value.getClass().getName());
            }
        }
        return bigDec;
    }

    /**
     * 过滤-只保留数字
     * @param context
     * @return
     */
    public static String getOnlyNumberByStr(String context) {
        return ONLY_NUMBER_PATTERN.matcher(context).replaceAll("").trim();
    }

    /**
     * 数字转boolean字符串
     * 0-否
     * 1-是
     * else:null
     * @param number
     * @return
     */
    public static String numberToBooleanStr(Number number){
        Boolean toBoolean = numberToBoolean(number);
        return toBoolean == null ? null : (toBoolean ? "是" : "否");
    }

    public static Boolean numberToBoolean(Number number){
        if (null == number){
            return null;
        }
        int i = number.intValue();
        switch (i){
            case 0:
                return false;
            case 1:
                return true;
            default:
                return null;
        }
    }
}
