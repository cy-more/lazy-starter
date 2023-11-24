package com.lazy.web.util;

import javax.servlet.http.HttpServletResponse;

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
}
