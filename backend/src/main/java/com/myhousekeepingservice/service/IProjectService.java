package com.myhousekeepingservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.entity.Project;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myhousekeepingservice.model.ProjectResp;

/**
* <p>
*   服务项目 服务类
* </p>
*
*/
public interface IProjectService extends IService<Project> {

    Page<ProjectResp> page(Integer pageNo, Integer pageSize, String name ,Long serviceId,Long companyId);

    void saveProject(Project project);
}
