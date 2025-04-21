package com.mysystem.ai.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysystem.ai.entity.SysAccess;
import com.mysystem.ai.mapper.SysAccessMapper;
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
        List<SysAccess> sysAccesses = sysAccessMapper.selectList(null);
        if (CollectionUtil.isEmpty(sysAccesses)) {
            SysAccess entity = new SysAccess();
            entity.setCount(0);
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
            entity.setAccessDate(formatter.format(today));
            sysAccessMapper.insert(entity);
        }
    }
}
