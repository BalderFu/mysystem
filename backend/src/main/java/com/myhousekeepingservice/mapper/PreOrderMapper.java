package com.myhousekeepingservice.mapper;

import com.myhousekeepingservice.entity.PreOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myhousekeepingservice.model.base.BaseStateCounter;
import com.myhousekeepingservice.model.base.StatsResp;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 预定单 Mapper 接口
 * </p>
 *
 * @author Jin
 * @since 2025-04-25
 */
@Mapper
public interface PreOrderMapper extends BaseMapper<PreOrder> {

    @Select("select t1.`name` `name`, count(*) `count` from `service_type` t1, `project` t2, `pre_order` t3, `service_tempo` t4 where t1.id = t2.service_id and t3.project_id = t2.id and t4.project_id = t3.id GROUP BY `name`")
    @MapKey("name")
    Map<String, Map<String,Object>> getServiceTypeCount();

    @Results({
            @Result(column = "name", property = "name"),
            @Result(column = "state", property = "state"),
            @Result(column = "count", property = "count"),
    })
    @Select("select t1.`name` `name`, t4.state state, count(*) count from `service_type` t1, `project` t2, `pre_order` t3, `service_tempo` t4  where t1.id = t2.service_id and t3.project_id = t2.id and t4.project_id = t3.id GROUP BY `name`, t4.state")
    List<BaseStateCounter>  statsSuccessRate();

}
