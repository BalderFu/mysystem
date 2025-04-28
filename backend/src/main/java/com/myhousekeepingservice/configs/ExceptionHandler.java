package com.myhousekeepingservice.configs;

import cn.dev33.satoken.exception.NotLoginException;
import com.myhousekeepingservice.model.base.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@Configuration
@RequiredArgsConstructor
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleException(Throwable e, HandlerMethod handlerMethod, HttpServletRequest request, HttpServletResponse response) {
        log.error("Request URI:{}, Method:{},Error occurs.", request.getRequestURI(), handlerMethod.getMethod().getName(), e);
        return Result.error(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleException(NotLoginException e, HandlerMethod handlerMethod, HttpServletRequest request, HttpServletResponse response) {
        log.error("Request URI:{}, Method:{},User not login. ", request.getRequestURI(), handlerMethod.getMethod().getName(), e);
        return Result.error(401, e.getMessage());
    }

}
