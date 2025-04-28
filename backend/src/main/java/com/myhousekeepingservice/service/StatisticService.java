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
//        List<ServiceType> serviceTypes = serviceTypeService.list();

//        Map<String, Map<String, Object>> typeCountMap = preOrderMapper.getServiceTypeCount();
//        List<StatsResp.UsageStat> usageStats = this.action(serviceTypes, s -> typeCountMap.containsKey(s.getName()), s -> new StatsResp.UsageStat(s.getName(), ((Long)typeCountMap.get(s.getName()).get("count")).intValue()), s -> new StatsResp.UsageStat(s.getName(), 0));
//        statsResp.setUsageStats(usageStats);

//        List<BaseStateCounter> counters = preOrderMapper.statsSuccessRate();
//        Map<String, List<BaseStateCounter>> map = counters.stream().collect(Collectors.groupingBy(BaseStateCounter::getName, Collectors.toList()));
//        List<StatsResp.SuccessRate> successRates = this.action(serviceTypes, s -> map.containsKey(s.getName()),
//                s -> {
//                    List<BaseStateCounter> list = map.getOrDefault(s.getName(), Collections.emptyList());
//                    return new StatsResp.SuccessRate(s.getName(), list.stream().filter(l -> l.getState() == 3).map(BaseStateCounter::getCount).findFirst().orElse(0), list.stream().filter(l -> l.getState() != 3).mapToInt(BaseStateCounter::getCount).sum());
//                }, s -> new StatsResp.SuccessRate(s.getName(), 0, 0));
//        statsResp.setSuccessRates(successRates);

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
