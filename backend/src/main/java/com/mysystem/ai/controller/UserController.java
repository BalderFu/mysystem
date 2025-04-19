package com.mysystem.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysystem.ai.entity.User;
import com.mysystem.ai.model.*;
import com.mysystem.ai.service.UserService;
import com.mysystem.ai.service.ValidateCodeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    @GetMapping("/pager")
    public Result<Page<User>> pager(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize, @RequestParam("username") String username) {
        return Result.success(userService.getBaseMapper().selectPage(new Page<User>(pageNo, pageSize), new LambdaQueryWrapper<User>().eq(StrUtil.isNotBlank(username), User::getUsername, username)));
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
        User u = userService.getById(loginId);
        if (!u.getRole().equals("admin")) {
            throw new RuntimeException("只有管理员能修改");
        }
        userService.updateById(user);
        return Result.success();
    }

    @GetMapping("/sendValidateCode")
    public Result<Void> sendValidateCode(@RequestParam("email") String email, HttpServletResponse response) throws IOException {
        validateCodeService.send(email, response);
        return Result.success();
    }


    @PostMapping("/add")
    public Result<Void> add(@RequestBody User user) {
        user.setNickname(user.getUsername());
        userService.getOneOpt(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())).ifPresent(u -> {
            throw new RuntimeException("用户名已存在");
        });
        userService.getOneOpt(new LambdaQueryWrapper<User>().eq(User::getEmail, user.getEmail())).ifPresent(u -> {
            throw new RuntimeException("邮箱已存在");
        });
        userService.save(user);
        return Result.success();
    }

    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam("userId") Long userId) {
        User user = userService.getById(StpUtil.getLoginIdAsLong());
        if (!user.getRole().equals("admin")) {
            throw new RuntimeException("只有管理员才能删除");
        }
        userService.removeById(userId);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody DeleteUsers deleteUsers) {
        List<Long> userIds = deleteUsers.getUserIds();
        userIds.remove(StpUtil.getLoginIdAsLong());
        userService.removeByIds(userIds);
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
