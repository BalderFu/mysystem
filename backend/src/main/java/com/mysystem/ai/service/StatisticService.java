package com.mysystem.ai.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mysystem.ai.entity.HashArithmetic;
import com.mysystem.ai.entity.SysAccess;
import com.mysystem.ai.entity.Usage;
import com.mysystem.ai.mapper.SysAccessMapper;
import com.mysystem.ai.mapper.UsageMapper;
import com.mysystem.ai.model.base.DateStatistic;
import com.mysystem.ai.model.base.StatsResp;
import com.mysystem.ai.model.base.TotalCountResp;
import com.mysystem.ai.model.base.UsageStatistic;
import com.mysystem.ai.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class StatisticService {

    @Autowired
    private SysAccessMapper sysAccessMapper;
    @Autowired
    private UsageMapper usageMapper;
    @Autowired
    private HashArithmeticService hashArithmeticService;
    @Autowired
    private UserService userService;

    public StatsResp getStats() {
        List<StatsResp.UsageStat> usageList = usageMapper.getUsageList().stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        List<UsageStatistic> usageStatisticList = usageMapper.getUsageStatisticList().stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        List<SysAccess> list = sysAccessMapper.list().stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        List<String> arithmetics = hashArithmeticService.list(new LambdaQueryWrapper<>()).stream().map(HashArithmetic::getKeyword).distinct().collect(Collectors.toList());
        List<String> usageNames = usageList.stream().map(StatsResp.UsageStat::getName).distinct().collect(Collectors.toList());

        List<StatsResp.UsageStat> usageStats = this.action(arithmetics, u -> !usageNames.contains(u), u -> new StatsResp.UsageStat(u, 0), u -> usageList.stream().filter(o -> o.getName().equals(u)).findFirst().orElse(null));

        List<String> list1 = usageStatisticList.stream().map(UsageStatistic::getArithmetic_name).distinct().collect(Collectors.toList());
        List<StatsResp.SuccessRate> successRates = this.action(arithmetics, u -> !list1.contains(u), u -> new StatsResp.SuccessRate(u,0,0), u -> usageStatisticList.stream().filter(s -> s.getArithmetic_name().equals(u)).findFirst().map(s -> new StatsResp.SuccessRate(s.getArithmetic_name(),s.getSuccess_count(),s.getFailure_count())).orElse(null));

        List<String> result = new ArrayList<>(30);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
        for (int i = 29; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            result.add(day.format(formatter));
        }
        List<Integer> counts = this.action(result, r -> list.stream().noneMatch(d-> d.getAccessDate().equals(r)), r ->  0, r -> list.stream().filter(d -> d.getAccessDate().equals(r)).map(SysAccess::getCount).findFirst().orElse(null));

        StatsResp statsResp = new StatsResp();
        statsResp.setUsageStats(usageStats);
        statsResp.setSuccessRates(successRates);

        StatsResp.AccessTrend accessTrend = new StatsResp.AccessTrend();
        accessTrend.setDates(result);
        accessTrend.setCounts(counts);
        statsResp.setAccessTrend(accessTrend);
        return statsResp;
    }

    public TotalCountResp totalCountResp() {
        TotalCountResp totalCountResp = new TotalCountResp();
        totalCountResp.setAlgorithmCount((int) hashArithmeticService.count());
        totalCountResp.setCrackTaskCount(usageMapper.selectCount(Wrappers.emptyWrapper()).intValue());
        totalCountResp.setUserCount(((int) userService.count()));
        totalCountResp.setTodayVisits(Optional.ofNullable(sysAccessMapper.selectOne(new LambdaQueryWrapper<SysAccess>().eq(SysAccess::getAccessDate, DateUtils.getToday()).last("limit 1"))).map(SysAccess::getCount).stream().findFirst().orElse(0));
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
