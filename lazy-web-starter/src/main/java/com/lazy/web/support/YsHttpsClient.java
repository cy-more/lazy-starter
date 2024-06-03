package com.lazy.web.support;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * @date: 2024-03-17 15:24
 * @description: TODO
 * @author: cy
 */
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class YsHttpsClient {

    private RestTemplate httpsTemplate;

    /**
     * post 表单请求
     * @param url
     * @param params
     * @return
     */
    public <T, V> V postForm(String url, T params, Class<V> responseClass){
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 创建请求实体
        HttpEntity<T> requestEntity = new HttpEntity<>(params, headers);

        // 发送请求并获取响应
        V response = httpsTemplate.postForObject(url, requestEntity, responseClass);

        log.info("Response: " + response);
        return response;
    }

    /**
     * post json请求
     * @param url
     * @param params
     * @return
     */
    public <T, V> V postJson(String url, T params, Class<V> responseClass){
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 创建请求实体
        HttpEntity<T> requestEntity = new HttpEntity<>(params, headers);

        // 发送请求并获取响应
        V response = httpsTemplate.postForObject(url, requestEntity, responseClass);
        log.info("Response: " + response);
        return response;
    }

}
