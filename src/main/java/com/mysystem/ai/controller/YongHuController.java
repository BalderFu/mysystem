package com.mysystem.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.mysystem.ai.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/user")
public class YongHuController {
    private static final String PATH = System.getProperty("user.dir") + "/src/main/resources" + File.separator + "upload";
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
    public Result<User> getUserInfo() {
        User user = yonghuService.getById(Long.valueOf((String) StpUtil.getLoginId()));
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
