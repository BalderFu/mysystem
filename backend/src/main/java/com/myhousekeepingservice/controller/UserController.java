package com.myhousekeepingservice.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.entity.User;
import com.myhousekeepingservice.model.base.*;
import com.myhousekeepingservice.service.UserService;
import com.myhousekeepingservice.service.ValidateCodeService;
import com.myhousekeepingservice.utils.UserUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateCodeService validateCodeService;

    @PostMapping("registry")
    public Result<String> registry(@Validated @RequestBody UserRegistry yonghuRegistry) {
        userService.registry(yonghuRegistry);
        return Result.success(StpUtil.getTokenValue());
    }

    @GetMapping("list")
    public Result<Map<Long, String>> list() {
        return Result.success(userService.list().stream().collect(Collectors.toMap(User::getId, User::getUsername)));
    }

    @PostMapping("login")
    public Result<String> login(@Validated @RequestBody UserRegistry yonghuRegistry) {
        userService.login(yonghuRegistry);
        return Result.success(StpUtil.getTokenValue());
    }

    @GetMapping("/pager")
    public Result<Page<User>> pager(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize, @RequestParam("username") String username) {
        User user = userService.getById(StpUtil.getLoginIdAsLong());
        List<String> userRoles = UserUtils.getUserRoles(user);
        return Result.success(userService.getBaseMapper().selectPage(new Page<User>(pageNo, pageSize), new LambdaQueryWrapper<User>().eq(StrUtil.isNotBlank(username), User::getUsername, username).in(User::getRole, userRoles)));
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
        Long loginId = StpUtil.getLoginIdAsLong();
        if (!loginId.equals(user.getId())) {
            User u = userService.getById(loginId);
            if (!u.getRole().equals("admin")) {
                throw new RuntimeException("只有管理员能修改");
            }
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

//    @PostMapping("/delete")
//    public Result<Void> delete(@RequestBody DeleteUsers deleteUsers) {
//        List<Long> userIds = deleteUsers.getUserIds();
//        userIds.remove(StpUtil.getLoginIdAsLong());
//        userService.removeByIds(userIds);
//        return Result.success();
//    }


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
}
