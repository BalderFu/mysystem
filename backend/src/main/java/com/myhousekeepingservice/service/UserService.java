package com.myhousekeepingservice.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myhousekeepingservice.entity.User;
import com.myhousekeepingservice.mapper.UserMapper;
import com.myhousekeepingservice.model.ResetPwsReq;
import com.myhousekeepingservice.model.UserRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    @Autowired
    private UserMapper userMapper;

    public void registry(UserRegistry userRegistry) {
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, userRegistry.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(userRegistry.getUsername());
        user.setPassword(userRegistry.getPassword());
        user.setEmail(userRegistry.getEmail());
        user.setPhone(userRegistry.getPhone());
        user.setNickname(userRegistry.getNickname());
        userMapper.insert(user);
        StpUtil.login(user.getId());
    }

    public boolean existPhone(String phone) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) > 0;
    }

    public boolean existEmail(String email) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, email)) > 0;
    }


    public void login(UserRegistry userRegistry) {
        List<User> list = userMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getUsername, userRegistry.getUsername()));
        if (CollectionUtil.isEmpty(list)) {
            throw new RuntimeException("用户不存在");
        }
        User yh = list.stream().filter(u -> u.getPassword().equals(userRegistry.getPassword())).findFirst().orElse(null);
        if (yh == null) {
            throw new RuntimeException("密码错误！");
        }
        StpUtil.login(yh.getId());
    }

    public void resetPassword(ResetPwsReq req) {
        Long loginId = Long.valueOf((String) StpUtil.getLoginId());
        User user = userMapper.selectById(loginId);
        if (ObjectUtil.isEmpty(user)) {
            throw new RuntimeException("用户已失效");
        }
        if (!StrUtil.equals(user.getPassword(), req.getOriginPassword())) {
            throw new RuntimeException("密码错误");
        }
        user.setPassword(req.getNewPassword());
        userMapper.updateById(user);
    }
}
