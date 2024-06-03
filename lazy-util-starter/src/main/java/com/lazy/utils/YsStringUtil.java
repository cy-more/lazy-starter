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
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern COMPLEX_PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@#$%^&*(),./;'\\[\\]\\-=])(?=\\S+$).{8,15}$");

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

    public static boolean isPhoneNum(String str){
        if (str == null || str.length() == 0) {
            return false;
        }
        Matcher matcher = PHONE_PATTERN.matcher(str);
        return matcher.find();
    }

    /**
     * 判断密码复杂度是否符合安全标准
     * 密码长度要大于8小于15，包含大小写字母数字和特殊字符
     * @param password
     * @return
     */
    public static boolean isComplexPassword(String password){
        if (password == null || password.length() == 0) {
            return false;
        }
        Matcher matcher = COMPLEX_PASSWORD_PATTERN.matcher(password);
        return matcher.find();
    }
}
