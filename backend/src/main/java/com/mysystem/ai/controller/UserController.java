package com.mysystem.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysystem.ai.entity.User;
import com.mysystem.ai.model.ResetPwsReq;
import com.mysystem.ai.model.Result;
import com.mysystem.ai.model.UserRegistry;
import com.mysystem.ai.model.ValidateLoginReq;
import com.mysystem.ai.service.UserService;
import com.mysystem.ai.service.ValidateCodeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private static final String PATH = System.getProperty("user.dir") + "/src/main/resources" + File.separator + "upload";
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateCodeService validateCodeService;

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

    @PostMapping("loginWithPhone")
    public Result<String> loginWithPhone(@Validated @RequestBody ValidateLoginReq validateLoginReq) {
        if (!userService.existEmail(validateLoginReq.getEmail())) {
            throw new RuntimeException("邮箱不存在");
        }
        validateCodeService.validate(validateLoginReq.getCode(), validateLoginReq.getEmail());
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, validateLoginReq.getEmail()).last("limit 1"));
        StpUtil.login(user.getId());
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

    @PutMapping("/update")
    public Result<Void> updateUserInfo(@Validated @RequestBody User user) {
        Long loginId = Long.valueOf((String) StpUtil.getLoginId());
        user.setId(loginId);
        userService.updateById(user);
        return Result.success();
    }

    @GetMapping("/sendValidateCode")
    public Result<Void> sendValidateCode(@RequestParam("email") String email, HttpServletResponse response) throws IOException {
        validateCodeService.send(email, response);
        return Result.success();
    }


    @PutMapping("/resetPws")
    public Result<Void> resetPws(@Validated @RequestBody ResetPwsReq req) {
        userService.resetPassword(req);
        return Result.success();
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
