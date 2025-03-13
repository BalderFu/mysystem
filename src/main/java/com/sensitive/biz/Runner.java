package com.sensitive.biz;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@SpringBootApplication
public class Runner implements ResponseBodyAdvice<String> {

    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
    }


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public String beforeBodyWrite(String body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Map<String, String> RESULT = new HashMap<String, String>() {{
            put("code", "200");
            put("message", "请求成功");
        }};
        if (returnType.getParameter().getType().isAssignableFrom(String.class)) {
            RESULT.put("data", body);
        }
        return JSONUtil.toJsonStr(RESULT);
    }


    @ExceptionHandler(Throwable.class)
    public Map<String, String> handleException(Throwable e) {
        log.error("Error occurs.", e);
        Map<String, String> RESULT = new HashMap<String, String>() {{
            put("code", "201");
        }};
        RESULT.put("message", e.getMessage());
        return RESULT;
    }
}
