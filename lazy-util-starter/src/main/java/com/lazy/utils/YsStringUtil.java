package com.lazy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author ：cy
 * @description ：字符串
 * @date ：2022/9/26 10:15
 */
public class YsStringUtil {

    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");
    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    /**
     * 驼峰转下划线,最后转为大写
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString().toUpperCase();
    }

    /**
     * 下划线转驼峰,正常输出
     * @param str
     * @return
     */
    public static String lineToHump(String str) {
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 判断是否是正则表达式
     * @param str
     * @return
     */
    public static boolean isRegex(String str) {
        try {
            Pattern.compile(str);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    /**
     * 判断是否是数字值的字符串
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        if (str == null || str.length() == 0) {
            return false;
        }
        return NUMBER_PATTERN.matcher(str).matches();
    }

    /**
     * 判断是否包含字母的字符串
     * @param str
     * @return
     */
    public static boolean isContainLetter(String str){
        if (str == null || str.length() == 0) {
            return false;
        }
        Matcher matcher = LETTER_PATTERN.matcher(str);
        return matcher.find();
    }


}
