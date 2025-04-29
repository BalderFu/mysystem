package com.myhousekeepingservice.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myhousekeepingservice.entity.SysAccess;
import com.myhousekeepingservice.mapper.SysAccessMapper;
import com.myhousekeepingservice.model.base.StatsResp;
import com.myhousekeepingservice.model.base.TotalCountResp;
import com.myhousekeepingservice.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class StatisticService {

    @Autowired
    private SysAccessMapper sysAccessMapper;
    @Autowired
    private UserService userService;

    public StatsResp getStats() {
        StatsResp statsResp = new StatsResp();
        statsResp.setUsageStats(List.of(
                new StatsResp.UsageStat("Test001", 100),
                new StatsResp.UsageStat("Test002", 300),
                new StatsResp.UsageStat("Test003", 800),
                new StatsResp.UsageStat("Test004", 400)
        ));
        statsResp.setSuccessRates(List.of(
                new StatsResp.SuccessRate("Test001", 20, 80),
                new StatsResp.SuccessRate("Test002", 30, 270),
                new StatsResp.SuccessRate("Test003", 80, 720),
                new StatsResp.SuccessRate("Test004", 50, 350)
        ));
        List<SysAccess> list = sysAccessMapper.list().stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        List<String> result = new ArrayList<>(30);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
        for (int i = 29; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            result.add(day.format(formatter));
        }
        List<Integer> counts = this.action(result, r -> list.stream().noneMatch(d -> d.getAccessDate().equals(r)), r -> 0, r -> list.stream().filter(d -> d.getAccessDate().equals(r)).map(SysAccess::getCount).findFirst().orElse(null));
        StatsResp.AccessTrend accessTrend = new StatsResp.AccessTrend();
        accessTrend.setDates(result);
        accessTrend.setCounts(counts);
        statsResp.setAccessTrend(accessTrend);
        return statsResp;
    }


    public TotalCountResp totalCountResp() {
        TotalCountResp totalCountResp = new TotalCountResp();
//        totalCountResp.setCount1((int) preOrderService.count(Wrappers.emptyWrapper()));
//        totalCountResp.setCount2((int) serviceTempoService.count(new LambdaQueryWrapper<ServiceTempo>().notIn(ServiceTempo::getState, 3)));
        totalCountResp.setCount3(((int) userService.count()));
        totalCountResp.setCount4(Optional.ofNullable(sysAccessMapper.selectOne(new LambdaQueryWrapper<SysAccess>().eq(SysAccess::getAccessDate, DateUtils.getToday()).last("limit 1"))).map(SysAccess::getCount).stream().findFirst().orElse(0));
        return totalCountResp;
    }


    private <T, R> List<R> action(List<T> list, Predicate<T> predicate, Function<T, R> func, Function<T, R> func2) {
        List<R> result = new ArrayList<>();
        for (T t : list) {
            if (predicate.test(t)) {
                R r = func.apply(t);
                result.add(r);
            } else {
                result.add(func2.apply(t));
            }
        }
        return result;
    }
}
