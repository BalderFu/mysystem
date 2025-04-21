package com.mysystem.ai.configs;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.mysystem.ai.entity.LogOperation;
import com.mysystem.ai.entity.User;
import com.mysystem.ai.service.LogOperationService;
import com.mysystem.ai.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Autowired
    @Lazy
    private LogOperationService logOperationService;
    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 包装请求和响应
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - start;

            String requestBody = getRequestBody(wrappedRequest);
            String responseBody = getResponseBody(wrappedResponse);
            wrappedResponse.copyBodyToResponse();
            Long userId = null;
            try {
                userId = StpUtil.getLoginIdAsLong();
            } catch (Exception e) {
            }
            try {
                saveLog(userId, request.getRequestURI(), StrUtil.isBlank(requestBody) ? request.getQueryString() : requestBody, new String(responseBody.getBytes(StandardCharsets.UTF_8)), duration);
            } catch (Exception e) {
                log.error("统计出错, ", e);
            }
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            return new String(buf, StandardCharsets.UTF_8);
        }
        return "";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            return new String(buf, StandardCharsets.UTF_8);
        }
        return "";
    }


    private void saveLog(Long userId, String uri, String req, String res, Long used) {
        User user;
        LogOperation logOperation = new LogOperation();
        logOperation.setUserId(userId == null ? -1L : userId);
        logOperation.setUsername(userId == null ? "未登陆用户" : (user = userService.getById(userId)) == null ? "用户信息不存在" : user.getUsername());
        logOperation.setUrl(uri);
        logOperation.setRequest(req);
        logOperation.setResponse(res);
        logOperation.setUsed((used));
        logOperationService.insert(logOperation);
    }
}

