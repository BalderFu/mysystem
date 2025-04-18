package com.mysystem.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysystem.ai.entity.LogOperation;
import com.mysystem.ai.mapper.LogOperationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogOperationService {
    @Autowired
    private LogOperationMapper logOperationMapper;

    public Page<LogOperation> pager(Long pageNo, Long pageSize) {
        Page<LogOperation> page = new Page<>(pageNo, pageSize);
        return logOperationMapper.selectPage(page, new LambdaQueryWrapper<LogOperation>().orderByDesc(LogOperation::getId));
    }

    public void insert(LogOperation logOperation) {
        logOperationMapper.insert(logOperation);
    }
}
