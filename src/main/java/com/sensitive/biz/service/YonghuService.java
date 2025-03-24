package com.sensitive.biz.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sensitive.biz.entity.Yonghu;
import com.sensitive.biz.mapper.YongHuMapper;
import com.sensitive.biz.model.YonghuRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YonghuService extends ServiceImpl<YongHuMapper, Yonghu> {
    @Autowired
    private YongHuMapper yongHuMapper;

    public void registry(YonghuRegistry yonghuRegistry) {
        if (yongHuMapper.selectCount(new LambdaQueryWrapper<Yonghu>().eq(Yonghu::getUsername, yonghuRegistry.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        Yonghu yonghu = new Yonghu();
        yonghu.setUsername(yonghuRegistry.getUsername());
        yonghu.setPassword(yonghuRegistry.getPassword());
        yonghu.setEmail(yonghuRegistry.getEmail());
        yonghu.setPhone(yonghuRegistry.getPhone());
        yonghu.setAvatar(yonghuRegistry.getAvatar());
        yongHuMapper.insert(yonghu);
        StpUtil.login(yonghu.getId());
    }


    public void login(YonghuRegistry yonghuRegistry) {
        List<Yonghu> list = yongHuMapper.selectList(new LambdaQueryWrapper<Yonghu>().eq(Yonghu::getUsername, yonghuRegistry.getUsername()));
        if (CollectionUtil.isEmpty(list)) {
            throw new RuntimeException("用户不存在");
        }
        Yonghu yh = list.stream().filter(yonghu -> yonghu.getPassword().equals(yonghuRegistry.getPassword())).findFirst().orElse(null);
        if (yh == null) {
            throw new RuntimeException("密码错误！");
        }
        StpUtil.login(yh.getId());
    }
}
