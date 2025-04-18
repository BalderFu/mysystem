package com.mysystem.ai.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Service
public class ValidateCodeService {
    private static final String CAPTCHA_SESSION_KEY = "captcha:";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;

    public void send(String email, HttpServletResponse response) throws IOException {
        if (!userService.existEmail(email)) {
            throw new RuntimeException("邮箱不存在");
        }
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 40, 5, 4);
        log.info("============登陆验证码：【{}】1分钟有效=====================", shearCaptcha.getCode());
        stringRedisTemplate.opsForValue().set(CAPTCHA_SESSION_KEY + email, shearCaptcha.getCode(), Duration.ofSeconds(60));
    }


    public void validate(String code, String phone) {
        String validateCode = stringRedisTemplate.opsForValue().get(CAPTCHA_SESSION_KEY + phone);
        if (StrUtil.isBlank(validateCode)) {
            throw new RuntimeException("验证码已过期");
        }
        if (!Objects.equals(validateCode, code)) {
           throw new RuntimeException("验证码错误");
        }
    }
}
