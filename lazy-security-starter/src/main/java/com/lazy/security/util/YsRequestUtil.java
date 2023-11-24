package com.lazy.security.util;

import com.lazy.exception.BizException;
import com.lazy.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ：cy
 * @description：request工具
 * @date ：2022/4/19 10:53
 */
public class YsRequestUtil {
    private static final String CONTENT_TYPE_FROM = "form";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final Set<String> METHODS_WITH_BODY = new HashSet<>(Arrays.asList("POST", "PUT", "PATCH", "DELETE"));
    /**
     * 获取请求Body
     *
     * @param request
     * @return
     */
    public static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))){
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LogUtil.logError(e);
            throw new BizException(e.getMessage());
        }
        return sb.toString();
    }


    /**
     * request转参数缓存支持
     * @param request
     * @return
     */
    public static HttpServletRequest transToCacheRequest(HttpServletRequest request){
        String contentType = request.getContentType();
        if (StringUtils.isBlank(contentType)){
        } else if (contentType.contains(CONTENT_TYPE_FROM)
                && !(request instanceof ParamReaderHttpServletRequestWrapper)) {
            request = new ParamReaderHttpServletRequestWrapper(request);
        } else if (contentType.startsWith(CONTENT_TYPE_JSON)
                && !(request instanceof BodyReaderHttpServletRequestWrapper)
                && hasRequestBody(request)) {
            request = new BodyReaderHttpServletRequestWrapper(request);
        }
        return request;
    }

    /**
     *  验证是否携带body
     * @param request
     * @return
     */
    public static boolean hasRequestBody(HttpServletRequest request) {
        String requestContentType = request.getContentType();
        String method = request.getMethod();
        return METHODS_WITH_BODY.contains(method) && requestContentType != null;
    }

}
