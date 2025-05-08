package com.lazy.utils;

import com.alibaba.fastjson.JSON;
import com.lazy.exception.BizException;
import com.lazy.utils.encryption.Sm4Utils;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author: cy
 * @description: 流处理工具类
 * @date: 2025-03-19 15:50
 **/
@Slf4j
public class YsIOUtil {

    /**
     * 加密+流式
     * 获取数据by流
     * @param file
     * @param clazz
     * @return
     * @param <T>
     */
    public static  <T> T getDataByIOCipher(InputStream file, Class<T> clazz, byte[] key){
        //文件读取
        try (CipherInputStream cis = new CipherInputStream(file, Sm4Utils.initCipher(Cipher.DECRYPT_MODE, key))) {
            // 使用字节数组输出流暂存解密后的数据
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] decryptedData = baos.toByteArray();

            // 确保解密后的数据是有效的 UTF-8 字符串
            String jsonString = new String(decryptedData, StandardCharsets.UTF_8);

            // 使用 Fastjson 解析 JSON
            return JSON.parseObject(jsonString, clazz);
        }catch (Exception e){
            throw new BizException("文件读取失败，e:" +e.getMessage());
        }
    }

    /**
     * 加密+流式
     * 获取流by数据
     * @param data
     * @return
     */
    public static InputStream getIOByDataCipher(Object data, byte[] key){
        try {
            return new ByteArrayInputStream(Sm4Utils.initCipher(Cipher.ENCRYPT_MODE, key).doFinal(JSON.toJSONBytes(data)));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BizException("加密流初始化失败");
        }
    }

}
