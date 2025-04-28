package com.myhousekeepingservice.service.impl;

import com.myhousekeepingservice.entity.Company;
import com.myhousekeepingservice.mapper.CompanyMapper;
import com.myhousekeepingservice.service.ICompanyService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
* <p>
*    家政 服务实现类
* </p>
*/
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements ICompanyService {

}
