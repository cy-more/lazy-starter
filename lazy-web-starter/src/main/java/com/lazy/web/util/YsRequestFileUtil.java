package com.lazy.web.util;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ：cy
 * @description：对外api处理工具类
 * @date ：2022/2/10 14:58
 */
public class YsRequestFileUtil {

    /**
     * 获取文件
     * @param httpRequest
     * @return
     */
    public static MultipartFile getFile(HttpServletRequest httpRequest, String fileKey) {
        if (httpRequest instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest request = (MultipartHttpServletRequest) httpRequest;
            return request.getFile(fileKey);
        }
        return null;
    }

    public static MultipartFile getFile(HttpServletRequest httpRequest) {
        return getFile(httpRequest, "file");
    }

    public static MultipartFile getImage(HttpServletRequest httpRequest){
        return getFile(httpRequest, "image");
    }

    public static void returnImage(HttpServletResponse response, BufferedImage image) throws IOException {
        ByteArrayOutputStream io = new ByteArrayOutputStream();
        ImageIO.write(image, "png", io);
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        os.write(io.toByteArray());
        os.flush();
        os.close();
    }

}
