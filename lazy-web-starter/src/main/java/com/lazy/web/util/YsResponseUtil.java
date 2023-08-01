package com.lazy.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lazy.utils.LogUtil;
import com.lazy.exception.ResultCode;
import com.lazy.entity.ResultMsg;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author ：cy
 * @description：响应信息组装
 * @date ：2021/10/26 15:43
 */
public class YsResponseUtil {

    /**
     * 响应处理
     */
    public static void failHandler(HttpServletResponse response
            , ResultCode resultCode) {
        ResultMsg<Object> resultMsg = ResultMsg.fail(resultCode.getCode(), resultCode.getMessage(), null);
        responseHandler(response, resultMsg);
    }

    /**
     * 响应处理
     */
    public static void failHandler(HttpServletResponse response
            , String code, String message) {
        ResultMsg<Object> resultMsg = ResultMsg.fail(code, message, null);
        responseHandler(response, resultMsg);
    }

    /**
     * 响应处理 success
     */
    public static void successHandler(HttpServletResponse response
            , Object data) {
        ResultMsg<Object> resultMsg = ResultMsg.ok(data);
        responseHandler(response, resultMsg);
    }

    /**
     * 响应处理
     */
    public static void responseHandler(HttpServletResponse response
            , Object resultMsg) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(200);

            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");

            PrintWriter out = response.getWriter();
            out.write(new ObjectMapper().writeValueAsString(resultMsg));
            out.flush();
            out.close();
        } catch (Exception e1) {
            LogUtil.logError(e1);
        }
    }

}
