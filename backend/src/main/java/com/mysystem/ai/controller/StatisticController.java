package com.mysystem.ai.controller;

import com.mysystem.ai.model.Result;
import com.mysystem.ai.model.base.StatsResp;
import com.mysystem.ai.model.base.TotalCountResp;
import com.mysystem.ai.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/arithmetic")
public class StatisticController {
    @Autowired
    private StatisticService statisticService;

    @GetMapping("/stats")
    public Result<StatsResp> getStatistic() {
        return Result.success(statisticService.getStats());
    }

    @GetMapping("/dashboard")
    public Result<TotalCountResp> dashboard() {
        return Result.success(statisticService.totalCountResp());
    }
}
