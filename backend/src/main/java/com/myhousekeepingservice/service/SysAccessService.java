package com.myhousekeepingservice.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myhousekeepingservice.entity.SysAccess;
import com.myhousekeepingservice.mapper.SysAccessMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SysAccessService extends ServiceImpl<SysAccessMapper, SysAccess> {
    @Autowired
    private SysAccessMapper sysAccessMapper;

    @PostConstruct
    public void init() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
        String format = formatter.format(today);
        List<SysAccess> sysAccesses = sysAccessMapper.selectList(new LambdaQueryWrapper<SysAccess>().eq(SysAccess::getAccessDate, format));
        if (CollectionUtil.isEmpty(sysAccesses)) {
            SysAccess entity = new SysAccess();
            entity.setCount(0);
            entity.setAccessDate(format);
            sysAccessMapper.insert(entity);
        }
    }
}
