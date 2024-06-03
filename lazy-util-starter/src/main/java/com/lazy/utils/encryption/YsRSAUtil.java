package com.lazy.utils.encryption;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author: cy
 * @description:
 * @date: 2024-04-07 11:23
 **/
public class YsRSAUtil {

    public static void main(String[] args) throws Exception {
        // 生成RSA密钥对
        KeyPair keyPair = generateRSAKeyPair();

        // 获取公钥和私钥字符串
        String publicKeyString = getPublicKeyString(keyPair.getPublic());
        String privateKeyString = getPrivateKeyString(keyPair.getPrivate());

        // 打印公钥和私钥字符串
        System.out.println("公钥: " + publicKeyString);
        System.out.println("私钥: " + privateKeyString);

        // 要加密的明文
        String plainText = "Hello, World!";

        // 使用公钥加密明文
        String encryptedText = encrypt(plainText, publicKeyString);
        System.out.println("加密后的内容: " + encryptedText);

        // 使用私钥解密密文
        String decryptedText = decrypt(encryptedText, privateKeyString);
        System.out.println("解密后的内容: " + decryptedText);
    }

    // 生成RSA密钥对
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.generateKeyPair();
    }

    // 获取公钥字符串
    public static String getPublicKeyString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // 获取私钥字符串
    public static String getPrivateKeyString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    // 使用公钥加密
    public static String encrypt(String plainText, String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 使用私钥解密
    public static String decrypt(String encryptedText, String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }
}
