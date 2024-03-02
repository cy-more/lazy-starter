package com.lazy.utils.encryption;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

import java.nio.charset.StandardCharsets;

/**
 * 处理加密数据-配置化 -cy
 * @author wy
 * @date 2022/8/3
 * @apiNode
 */
public class Sm4Utils {

    private byte[] SM4KEYS;

    public Sm4Utils(String sm4KeyStr) {
        this.SM4KEYS = sm4KeyStr.getBytes(StandardCharsets.UTF_8);
    }

    public String sm4EncryData(String data){
        if (null == data){
            return null;
        }
        SymmetricCrypto sm4 = SmUtil.sm4(SM4KEYS);
        String encrypt = sm4.encryptHex(data);
        return encrypt;
    }
    
    public String sm4DecryData(String data){
        if (null == data) {
            return null;
        }
        try {
            SymmetricCrypto sm4 = SmUtil.sm4(SM4KEYS);
            return sm4.decryptStr(data, CharsetUtil.CHARSET_UTF_8);
        }catch (Exception e){
            return data;
        }
    }
    
}
