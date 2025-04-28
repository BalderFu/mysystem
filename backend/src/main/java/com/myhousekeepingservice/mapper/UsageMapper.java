package com.myhousekeepingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myhousekeepingservice.entity.Usage;
import com.myhousekeepingservice.model.base.StatsResp;
import com.myhousekeepingservice.model.base.UsageStatistic;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UsageMapper extends BaseMapper<Usage> {

    @Select("SELECT  arithmetic_id,  arithmetic_name name, COUNT(*) AS value FROM `usage` GROUP BY  arithmetic_id, arithmetic_name;")
    List<StatsResp.UsageStat> getUsageList();


    @Results({@Result(column = "arithmetic_id", property = "arithmetic_id"),@Result(column = "arithmetic_name", property = "arithmetic_name"),@Result(column = "success_count", property = "success_count"),@Result(column = "failure_count", property = "failure_count")})
    @Select("SELECT  arithmetic_id, arithmetic_name, SUM(state = 1) AS success_count, SUM(state = 0) AS failure_count FROM  `usage` GROUP BY  arithmetic_id, arithmetic_name")
    List<UsageStatistic> getUsageStatisticList();
}
