package com.lazy.web.util;

import com.lazy.utils.YsLogUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: cy
 * @description:
 * @date: 2023-10-28 13:19
 **/
public class YsResponseUtil {

    public static void write(String message, HttpServletResponse response) throws Exception {
        // 设置缓存区编码为UTF-8编码格式
        response.setCharacterEncoding("UTF-8");
        // 在响应中主动告诉浏览器使用UTF-8编码格式来接收数据
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        // 可以使用封装类简写Content-Type，使用该方法则无需使用setCharacterEncoding
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(message);
    }


    public static void write(HttpServletResponse response, String message) throws Exception {
        // 设置缓存区编码为UTF-8编码格式
        response.setCharacterEncoding("UTF-8");
        // 在响应中主动告诉浏览器使用UTF-8编码格式来接收数据
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        // 可以使用封装类简写Content-Type，使用该方法则无需使用setCharacterEncoding
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(message);
    }

    public static void export(HttpServletResponse response, InputStream inputStream, String fileName){
        // 设置响应头信息
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        // 读取文件内容并写入到输出流中
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            YsLogUtil.logError(e);
        }finally {
            // 关闭输入输出流
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                YsLogUtil.logError(e);
            }
        }
    }

    public static void export(HttpServletResponse response, byte[] bytes, String fileName){
        // 设置响应头信息
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        // 读取文件内容并写入到输出流中
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            YsLogUtil.logError(e);
        }finally {
            // 关闭输入输出流
            try {
                outputStream.close();
            } catch (IOException e) {
                YsLogUtil.logError(e);
            }
        }
    }
}
