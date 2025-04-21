package com.mysystem.ai.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysystem.ai.entity.HashArithmetic;
import com.mysystem.ai.entity.HashDict;
import com.mysystem.ai.entity.Usage;
import com.mysystem.ai.mapper.HashArithmeticMapper;
import com.mysystem.ai.mapper.HashDictMapper;
import com.mysystem.ai.mapper.UsageMapper;
import com.mysystem.ai.model.CrackReq;
import com.mysystem.ai.utils.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HashDictService extends ServiceImpl<HashDictMapper, HashDict> {
    @Autowired
    private HashDictMapper hashDictMapper;
    @Autowired
    private HashArithmeticMapper hashArithmeticMapper;
    @Autowired
    private UsageMapper usageMapper;

    public void saveDict(HashDict hashDict) {
        hashDict.setUserId(StpUtil.getLoginIdAsLong());
        if (Boolean.TRUE.equals(hashDict.getAutoGenerateHash())) {
            HashArithmetic hashArithmetic = hashArithmeticMapper.selectById(hashDict.getArithmeticId());
            if (ObjectUtil.isEmpty(hashArithmetic)) {
                throw new RuntimeException("算法不存在");
            }
            Method method = ReflectUtil.getMethod(DigestUtils.class, hashArithmetic.getKeyword(), String.class);
            String value = ReflectUtil.invokeStatic(method, hashDict.getHashKey());
            hashDict.setHashValue(value);
        }
        hashDictMapper.insert(hashDict);
    }


    public List<String> crack(CrackReq crackReq) {
        HashArithmetic hashArithmetic = hashArithmeticMapper.selectById(crackReq.getId());
        if (ObjectUtil.isEmpty(hashArithmetic)) {
            throw new RuntimeException("算法不存在");
        }
        List<String> list = hashDictMapper.selectList(new LambdaQueryWrapper<HashDict>().eq(HashDict::getArithmeticId, crackReq.getId()).eq(HashDict::getHashValue, crackReq.getHashText()))
                .stream().map(HashDict::getHashKey).collect(Collectors.toList());
        Usage usage = new Usage();
        usage.setArithmeticId(crackReq.getId());
        usage.setArithmeticName(hashArithmetic.getKeyword());
        usage.setState(CollectionUtil.isNotEmpty(list));
        usageMapper.insert(usage);
        return list;
    }
}
