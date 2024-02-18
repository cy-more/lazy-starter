package com.lazy.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：cy
 * @description：二维码/条形码
 * @date ：2021/10/8 15:25
 */
public class YsQRCodeUtils {
    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";


    /**
     * 二维码生成
     * @param width
     * @param height
     * @param content
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static BufferedImage createQRCode(int width, int height, String content) throws WriterException {
         // 二维码基本参数设置
         Map<EncodeHintType, Object> hints = new HashMap<>();
        // 设置编码字符集utf-8
         hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 设置纠错等级L/M/Q/H,纠错等级越高越不易识别，当前设置等级为最高等级H
         hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        // 可设置范围为0-10，但仅四个变化0 1(2) 3(4 5 6) 7(8 9 10)
         hints.put(EncodeHintType.MARGIN, 0);
         // 生成图片类型为QRCode
         BarcodeFormat format = BarcodeFormat.QR_CODE;
         // 创建位矩阵对象
         BitMatrix bitMatrix = new MultiFormatWriter().encode(content, format, width, height, hints);
         // 设置位矩阵转图片的参数
         //        MatrixToImageConfig config = new MatrixToImageConfig(Color.black.getRGB(), Color.white.getRGB());

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 条形码
     * @param width
     * @param height
     * @param content
     * @return
     * @throws WriterException
     */
    public static BufferedImage getBarcode(Integer width, Integer height, String content) throws WriterException{
        // 条形码基本参数设置
        Map<EncodeHintType, Object> hints = new HashMap<>();
        // 设置编码字符集utf-8
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 设置纠错等级L/M/Q/H,纠错等级越高越不易识别，当前设置等级为最高等级H
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 可设置范围为0-10，但仅四个变化0 1(2) 3(4 5 6) 7(8 9 10)
        hints.put(EncodeHintType.MARGIN, 1);

        /* Try to encode the string as a barcode */
        Code128Writer writer = new Code128Writer();
        BitMatrix bar = writer.encode(content, BarcodeFormat.CODE_128, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bar);
    }


}
