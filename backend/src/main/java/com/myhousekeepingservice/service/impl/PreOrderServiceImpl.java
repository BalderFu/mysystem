package com.myhousekeepingservice.service.impl;

import com.myhousekeepingservice.entity.PreOrder;
import com.myhousekeepingservice.mapper.PreOrderMapper;
import com.myhousekeepingservice.service.IPreOrderService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
* <p>
*    预定单 服务实现类
* </p>
*/
@Service
public class PreOrderServiceImpl extends ServiceImpl<PreOrderMapper, PreOrder> implements IPreOrderService {

}
