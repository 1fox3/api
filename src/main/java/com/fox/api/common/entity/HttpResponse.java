package com.fox.api.common.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 请求响应结果
 */
public class HttpResponse {
    private int code;
    private Map<String, List<String>> headers;
    private String requestUrl;
    private String content;

    public HttpResponse(int code, Map<String, List<String>> headers, String requestUrl, String content) {
        this.code = code;
        this.headers = headers;
        this.requestUrl = requestUrl;
        this.content = content;
    }

    public <T> T getContent(Class<T> clz) throws IOException {
        if (StringUtils.isNotBlank(content)) {
            return new ObjectMapper().readValue(content, clz);
        }
        return null;
    }

    /**
     * 获取响应状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取响应头
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * 获取最后请求地址
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * 获取响应内容
     */
    public String getContent() {
        return content;
    }
}
