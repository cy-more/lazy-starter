package com.lazy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;

/**
 * @author zhangzongdong
 * @date 2019/12/21 16:23
 * @email zzdvip88@aliyun.com
 */
@Slf4j
public class YsAlgorithmUtils {

    /**
     * 对字符串进行MD5编码
     */
    public static String encodeByMD5(String originString) {
        if (StringUtils.isNotBlank(originString)) {
            try {
                // 创建具有指定算法名称的信息摘要
                MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
                // 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
                byte[] results = md5.digest(originString.getBytes());
                // 将得到的字节数组变成字符串返回
                return byteArrayToHexString(results);
            } catch (Exception e) {
                log.error("encodeByMD5 error");
            }
        }
        return "";
    }

    /**
     * MD5+Salt加密
     *
     * @param content
     * @return
     */
    public static String encryptMD5WithSalt(String content) {

        String resultString = "";
        String appKey = "fdjf,jkgfkl";

        byte[] a = appKey.getBytes();
        byte[] datSource = content.getBytes();
        byte[] b = new byte[a.length + datSource.length];

        int i;
        for (i = 0; i < datSource.length; i++) {
            b[i] = datSource[i];
        }

        for (int k = 0; k < a.length; k++) {
            b[i] = a[k];
            i++;
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
            md5.update(b);

            // 将得到的字节数组变成字符串返回
            // 转为十六进制的字符串
            resultString = new HexBinaryAdapter().marshal(md5.digest());
        } catch (Exception e) {
            log.error("encryptMD5WithSalt error");
        }

        return resultString.toLowerCase();
    }

    /**
     * 转换字节数组为十六进制字符串
     *
     * @param b
     *            字节数组
     * @return 十六进制字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    /**
     * 将一个字节转化成十六进制形式的字符串
     *
     * @param b
     * @return
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 十六进制下数字到字符的映射数组
     */
    private final static String[] hexDigits =
        {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static final String KEY_MD5 = "MD5";

    private YsAlgorithmUtils() {}
}
