package com.sau.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 响应处理工具类
 */
public class ResponseUtils {

    /**
     * @param response 响应对象
     * @param contentType 内容类型（如application/json;charset=UTF-8）
     * @param status 响应状态码（如401）
     * @param message 响应体内容
     * @throws IOException 可能的IO异常
     */
    public static void writeResponse(HttpServletResponse response, String contentType, int status, String message) throws IOException {

        response.setContentType(contentType);
        response.setStatus(status);
        response.getWriter().write(message);
    }
}