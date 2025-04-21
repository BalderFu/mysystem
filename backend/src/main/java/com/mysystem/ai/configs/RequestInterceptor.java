package com.mysystem.ai.configs;

import com.mysystem.ai.mapper.SysAccessMapper;
import com.mysystem.ai.utils.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    @Autowired
    @Lazy
    private SysAccessMapper sysAccessMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        sysAccessMapper.updateCount(DateUtils.getToday());
        return true;
    }
}
