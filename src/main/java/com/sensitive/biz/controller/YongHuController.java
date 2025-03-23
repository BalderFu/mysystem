package com.sensitive.biz.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sensitive.biz.model.Result;
import com.sensitive.biz.entity.Yonghu;
import com.sensitive.biz.model.YonghuRegistry;
import com.sensitive.biz.service.YonghuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class YongHuController {
    @Autowired
    YonghuService yonghuService;

    @PostMapping("registry")
    public Result<String> registry(@Validated @RequestBody YonghuRegistry yonghuRegistry) {
        yonghuService.registry(yonghuRegistry);
        return Result.success(StpUtil.getTokenValue());
    }

    @PostMapping("login")
    public Result<String> login(@Validated @RequestBody YonghuRegistry yonghuRegistry) {
        yonghuService.login(yonghuRegistry);
        return Result.success(StpUtil.getTokenValue());
    }

    @GetMapping("/userInfo")
    public Result<Yonghu> getUserInfo() {
        Yonghu yonghu = yonghuService.getById(Long.valueOf((String) StpUtil.getLoginId()));
        if (yonghu.getRole().equals("admin")) {
            yonghu.setRole("管理员");
        } else {
            yonghu.setRole("普通用户");
        }
        return Result.success(yonghu);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success();
    }
}
