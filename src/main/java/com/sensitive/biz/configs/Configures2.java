package com.sensitive.biz.configs;

import cn.dev33.satoken.exception.NotLoginException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@Configuration
@RequiredArgsConstructor
public class Configures2{

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> handleException(Throwable e) {
        log.error("Error occurs.", e);
        Map<String, String> RESULT = new HashMap<String, String>() {{
            put("code", "201");
        }};
        RESULT.put("message", e.getMessage());
        return RESULT;
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> handleException(NotLoginException e) {
        log.error("User not login.", e);
        Map<String, String> RESULT = new HashMap<String, String>() {{
            put("code", "401");
        }};
        RESULT.put("message", e.getMessage());
        return RESULT;
    }

}
