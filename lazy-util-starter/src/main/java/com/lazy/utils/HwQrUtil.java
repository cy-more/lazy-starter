package com.lazy.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

/**
 * @author chenjun
 * @date 2022/3/17 9:55
 * @description: 华为二维码生成工具
 */
public class HwQrUtil {

    private static BufferedImage scaleImage;
    private static BufferedImage image;

    static {
        try {
            //初始化logo图片信息
            scaleImage = scale(new ClassPathResource("qr/logo1.png").getInputStream(), 180, 180, false);
            //初始化模板信息
            image = ImageIO.read(new ClassPathResource("qr/template.jpg").getInputStream());
            //设置字体
            Font font = Font.createFont(Font.TRUETYPE_FONT, new ClassPathResource("qr/Alibaba-PuHuiTi-Bold.otf").getInputStream());
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }

    /**
     * 创建华为二维码
     *
     * @param qrContext  二维码内容
     * @param width      二维码图片宽度
     * @param height     二维码图片高度
     * @param logoWidth  logo图片宽度
     * @param logoHeight logo图片高度
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static BufferedImage createQrAndLogo(String qrContext, int width, int height, int logoWidth, int logoHeight) throws WriterException, IOException {
        int[][] srcPixels = new int[logoWidth][logoHeight];
        for (int i = 0; i < scaleImage.getWidth(); i++) {
            for (int j = 0; j < scaleImage.getHeight(); j++) {
                srcPixels[i][j] = scaleImage.getRGB(i, j);
            }
        }

        Map<EncodeHintType, Object> hint = new HashMap<>(4);
        hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hint.put(EncodeHintType.MARGIN, 0);

        // 生成二维码
        BitMatrix matrix = new MultiFormatWriter().encode(qrContext, BarcodeFormat.QR_CODE, width, height, hint);

        // 二维矩阵转为一维像素数组
        int halfW = matrix.getWidth() / 2;
        int halfH = matrix.getHeight() / 2;
        int[] pixels = new int[width * height];
        int logoHalfWidth = logoWidth / 2;

        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                // 读取图片
                if (x > halfW - logoHalfWidth && x < halfW + logoHalfWidth && y > halfH - logoHalfWidth && y < halfH + logoHalfWidth) {
                    pixels[y * width + x] = srcPixels[x - halfW + logoHalfWidth][y - halfH + logoHalfWidth];
                } else {
                    // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                    pixels[y * width + x] = matrix.get(x, y) ? BLACK.getRGB() : WHITE.getRGB();
                }
            }
        }
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getRaster().setDataElements(0, 0, width, height, pixels);
        BufferedImage image = setClip(bufferedImage, 40);

        return image;
    }

    /**
     * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
     *
     * @param inputStream 源文件流
     * @param height      目标高度
     * @param width       目标宽度
     * @param hasFiller   比例不对时是否需要补白：true为补白; false为不补白;
     * @return
     * @throws IOException
     */
    private static BufferedImage scale(InputStream inputStream, int height, int width, boolean hasFiller) throws IOException {
        // 缩放比例
        double ratio;
        BufferedImage srcImage = ImageIO.read(inputStream);
        Image destImage = srcImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        // 计算比例
        if ((srcImage.getHeight() > height) || (srcImage.getWidth() > width)) {
            if (srcImage.getHeight() > srcImage.getWidth()) {
                ratio = (new Integer(height)).doubleValue() / srcImage.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue() / srcImage.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            destImage = op.filter(srcImage, null);
        }
        // 补白
        if (hasFiller) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphic = image.createGraphics();
            graphic.setColor(Color.white);
            graphic.fillRect(0, 0, width, height);
            if (width == destImage.getWidth(null)) {
                graphic.drawImage(destImage, 0, (height - destImage.getHeight(null)) / 2, destImage.getWidth(null), destImage.getHeight(null), Color.white, null);
            } else {
                graphic.drawImage(destImage, (width - destImage.getWidth(null)) / 2, 0, destImage.getWidth(null), destImage.getHeight(null), Color.white, null);
                graphic.dispose();
                destImage = image;
            }
        }
        return (BufferedImage) destImage;
    }

    /**
     * 将图片变为圆角
     *
     * @param bufferedImage 源文件图形流
     * @param radius        圆角度
     * @return
     */
    public static BufferedImage setClip(BufferedImage bufferedImage, int radius) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = image.createGraphics();

        gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gs.setClip(new RoundRectangle2D.Double(0, 0, width, height, radius, radius));
        gs.drawImage(bufferedImage, 0, 0, null);
        gs.dispose();
        return image;
    }

    /**
     * 添加在指定位置追加文字水印
     *
     * @param text          要添加的文字
     * @param bufferedImage 目标
     * @param fontName      字体
     * @param fontStyle     样式
     * @param color         字体颜色
     * @param fontSize      字体大小
     * @param x             x坐标
     * @param y             y坐标
     * @return
     */
    public static BufferedImage addWaterText(String text, BufferedImage bufferedImage, String fontName, int fontStyle, Color color, int fontSize, int x, int y) {
        // 目标文件
        int width = bufferedImage.getWidth(null);
        int height = bufferedImage.getHeight(null);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(bufferedImage, 0, 0, width, height, null);
        g.setColor(color);
        g.setFont(new Font(fontName, fontStyle, fontSize));
        g.drawString(text, x, y + fontSize);
        g.dispose();
        return bufferedImage;
    }

    /**
     * 添加模板
     *
     * @param img 目标
     * @param x   x坐标
     * @param y   y坐标
     * @return
     */
    public final static BufferedImage addTemple(BufferedImage img, int x, int y) {
        // 目标文件
        BufferedImage bufferedImage = copyImage(image);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(img, x, y, 950, 950, null);
        g.dispose();
        return bufferedImage;
    }

    /**
     * 图片拷贝
     *
     * @param source
     * @return
     */
    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

}
