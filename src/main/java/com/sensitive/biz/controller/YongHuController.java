package com.sensitive.biz.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.sensitive.biz.model.Result;
import com.sensitive.biz.entity.Yonghu;
import com.sensitive.biz.model.YonghuRegistry;
import com.sensitive.biz.service.YonghuService;
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
