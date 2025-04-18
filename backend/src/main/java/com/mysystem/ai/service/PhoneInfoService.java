package com.mysystem.ai.service;

import com.mysystem.ai.entity.PhoneInfo;
import com.mysystem.ai.mapper.PhoneInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneInfoService {

    @Autowired
    private PhoneInfoMapper phoneInfoMapper;

    public void save(PhoneInfo phoneInfo) {
        phoneInfoMapper.insert(phoneInfo);
    }
}
