package com.lazy.crud.util;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ：cy
 * @description：AES加密
 * @date ：2021/9/28 13:54
 */
@Component
public class AESUtil {
    private static final String AES_KEY = "AES_KEY";

    private static final byte[] KEY_BYTES;

    @Value("${mybatis.encrypt.aes.key}")
    private static String keyStr = "ad1725339b2dd0a68903c57b635942ca";

    static {
        KEY_BYTES = new byte[16];
        int i = 0;
        for (byte b : keyStr.getBytes()) {
            KEY_BYTES[i++ % 16] ^= b;
        }
    }

    public static String encrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return HexUtil.encodeHexStr(SecureUtil.aes(KEY_BYTES).encrypt(content), false);
    }

    public static String decrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return SecureUtil.aes(KEY_BYTES).decryptStr(content);
    }

}
