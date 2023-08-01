package com.lazy.utils;

import com.lazy.exception.BizException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author ：cy
 * @description：图片工具
 * @date ：2021/10/9 14:45
 */
public class ImageUtil {
    /**
     * BufferedImage转byte[]
     *
     * @param bImage BufferedImage对象
     * @return byte[]
     * @auth zhy
     */
    public static byte[] imageToBytes(BufferedImage bImage) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", out);
        } catch (IOException e) {
            LogUtil.logError(e);
            throw new BizException(e.getMessage());
        }
        return out.toByteArray();
    }


    /**
     * BufferedImage转byte[],指定编码格式
     *
     * @param bImage
     * @param formatName
     * @return
     */
    public static byte[] imageToBytesByFormatName(BufferedImage bImage, String formatName) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, formatName, out);
        } catch (IOException e) {
            LogUtil.logError(e);
            throw new BizException(e.getMessage());
        }
        return out.toByteArray();
    }
}
