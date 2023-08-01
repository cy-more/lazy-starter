package com.lazy.web.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author ：cy
 * @description：web流参数包装
 * @date ：2022/7/22 9:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YsApiFile {

    String fileUrl;

    MultipartFile file;

    public YsApiFile(HttpServletRequest httpRequest) {
        this(httpRequest, "file");
    }

    public YsApiFile(HttpServletRequest httpRequest, String paramKey) {
        this.fileUrl = (null != httpRequest.getParameterMap().get(paramKey) && file == null) ? "" : null;
        this.file = getFile(httpRequest, paramKey);
    }

    /**
     * 防空指针
     * @param apiFile
     * @return
     */
    public static YsApiFile ofNullable(YsApiFile apiFile){
        return Optional.ofNullable(apiFile).orElse(new YsApiFile());
    }

    /**
     * 文件上传
     * @param uploadFun T:流文件  R:上传地址
     */
    public YsApiFile uploadFile(Function<MultipartFile, String> uploadFun) {
        if (file == null){
            return this;
        }
        String fileUrl = uploadFun.apply(file);

        if (StringUtils.isEmpty(fileUrl)){
            return this;
        }

        this.fileUrl = fileUrl;
        return this;
    }

    /**
     * 根据request组装file
     * @param httpRequest
     * @param fileKey
     * @return
     */
    private MultipartFile getFile(HttpServletRequest httpRequest, String fileKey) {
        if (httpRequest instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest request = (MultipartHttpServletRequest) httpRequest;
            return request.getFile(fileKey);
        }
        return null;
    }
}
