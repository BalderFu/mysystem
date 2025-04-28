package com.myhousekeepingservice.service.impl;

import com.myhousekeepingservice.entity.ServiceType;
import com.myhousekeepingservice.mapper.ServiceTypeMapper;
import com.myhousekeepingservice.service.IServiceTypeService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
* <p>
*    家政 服务实现类
* </p>
*/
@Service
public class ServiceTypeServiceImpl extends ServiceImpl<ServiceTypeMapper, ServiceType> implements IServiceTypeService {

}
