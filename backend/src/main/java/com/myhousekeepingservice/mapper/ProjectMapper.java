package com.myhousekeepingservice.mapper;

import com.myhousekeepingservice.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myhousekeepingservice.model.ProjectResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 服务项目 Mapper 接口
 * </p>
 *
 * @author Jin
 * @since 2025-04-25
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    List<ProjectResp> selectPage(@Param("size")Integer size, @Param("no")Integer no, @Param("name")String name, @Param("serviceId")Long serviceId, @Param("companyId")Long companyId);

    Integer count(@Param("size")Integer size, @Param("no")Integer no, @Param("name")String name, @Param("serviceId")Long serviceId, @Param("companyId")Long companyId);

}
