package com.myhousekeepingservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myhousekeepingservice.entity.Usage;
import com.myhousekeepingservice.mapper.UsageMapper;
import org.springframework.stereotype.Service;

@Service
public class UsageService extends ServiceImpl<UsageMapper, Usage> {
}
