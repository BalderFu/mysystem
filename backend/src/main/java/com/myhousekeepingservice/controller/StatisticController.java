package com.myhousekeepingservice.controller;

import com.myhousekeepingservice.model.Result;
import com.myhousekeepingservice.model.base.StatsResp;
import com.myhousekeepingservice.model.base.TotalCountResp;
import com.myhousekeepingservice.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistic")
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
