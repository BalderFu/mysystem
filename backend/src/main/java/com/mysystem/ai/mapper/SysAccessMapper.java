package com.mysystem.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysystem.ai.entity.SysAccess;
import com.mysystem.ai.model.base.DateStatistic;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface SysAccessMapper extends BaseMapper<SysAccess> {

    @Select("SELECT   `access_date` accessDate, `count`  FROM  `sys_access` WHERE create_time >= CURDATE() - INTERVAL 30 DAY  ORDER BY  access_date ASC")
    List<SysAccess> list();

    @Update("update `sys_access` set count = count + 1 where access_date = #{date}")
    void updateCount(@Param("date")String date);
}
