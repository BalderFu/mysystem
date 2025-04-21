package com.mysystem.ai.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysystem.ai.entity.Usage;
import com.mysystem.ai.mapper.UsageMapper;
import org.springframework.stereotype.Service;

@Service
public class UsageService extends ServiceImpl<UsageMapper, Usage> {
}
