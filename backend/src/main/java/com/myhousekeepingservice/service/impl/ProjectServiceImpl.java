package com.myhousekeepingservice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.entity.Company;
import com.myhousekeepingservice.entity.Project;
import com.myhousekeepingservice.mapper.ProjectMapper;
import com.myhousekeepingservice.model.ProjectResp;
import com.myhousekeepingservice.service.ICompanyService;
import com.myhousekeepingservice.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 服务项目 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {
    private final ProjectMapper projectMapper;
    private final ICompanyService companyService;

    @Override
    public Page<ProjectResp> page(Integer pageNo, Integer pageSize, String name, Long serviceId, Long companyId) {
        Integer count = projectMapper.count(pageSize, pageNo, name, serviceId, companyId);
        if (count > 0) {
            Page<ProjectResp> projectRespPage = new Page<>(pageNo, pageSize, count);
            projectRespPage.setRecords(projectMapper.selectPage(pageSize, pageNo, name, serviceId, companyId));
            return projectRespPage;
        }
        return new Page<>(pageNo, pageSize, 0);
    }

    @Override
    public void saveProject(Project project) {
        Company company = companyService.getById(project.getCompanyId());
        if (ObjectUtil.isEmpty(company)) {
            throw new RuntimeException("家政公司信息不存在");
        }
        if (Boolean.FALSE.equals(company.getState())) {
            throw new RuntimeException("该家政不可预约！");
        }
//        if (!company.getTime().equals("随时")) {
//            String[] split = company.getTime().split("-");
//            if (project.getTime().equals("随时")) {
//                throw new RuntimeException("家政可约时间在:" + company.getTime());
//            }
//            if (split.length == 1) {
//                throw new RuntimeException("家政信息数据存在错误:" + company.getTime());
//            }
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//            if (LocalDate.parse(split[0], formatter).isAfter(LocalDate.parse(project.getTime().split("-")[0], formatter))) {
//                throw new RuntimeException("家政可约时间开始时间是：" + split[0]);
//            }
//            if (LocalDate.parse(split[1], formatter).isBefore(LocalDate.parse(project.getTime().split("-")[1], formatter))) {
//                throw new RuntimeException("家政可约时间结束时间是：" + split[0]);
//            }
//        }
        projectMapper.insert(project);
    }
}
