package com.mysystem.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.mysystem.ai.entity.User;
import com.mysystem.ai.model.Result;
import com.mysystem.ai.model.UserRegistry;
import com.mysystem.ai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private static final String PATH = System.getProperty("user.dir") + "/src/main/resources" + File.separator + "upload";
    @Autowired
    private UserService userService;

    @PostMapping("registry")
    public Result<String> registry(@Validated @RequestBody UserRegistry yonghuRegistry) {
        userService.registry(yonghuRegistry);
        return Result.success(StpUtil.getTokenValue());
    }

    @PostMapping("login")
    public Result<String> login(@Validated @RequestBody UserRegistry yonghuRegistry) {
        userService.login(yonghuRegistry);
        return Result.success(StpUtil.getTokenValue());
    }

    @GetMapping("/userInfo")
    public Result<User> getUserInfo() {
        User user = userService.getById(Long.valueOf((String) StpUtil.getLoginId()));
        if (user.getRole().equals("admin")) {
            user.setRole("管理员");
        } else {
            user.setRole("普通用户");
        }
        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success();
    }

    @PostMapping("/upload")
    public Result<String> sensitive(@RequestParam("file") MultipartFile file) {
        File uploadFolder = new File(PATH);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg")) {
            throw new RuntimeException("只允许上传 [png、jpg、jpeg] 文件！");
        }
        fileName = IdUtil.fastSimpleUUID() + "." + extension;
        File dest = new File(uploadFolder, fileName);
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传出错");
        }
        return Result.success(fileName);
    }
}
