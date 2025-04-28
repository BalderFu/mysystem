package com.myhousekeepingservice.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class StatsResp {
    private List<UsageStat> usageStats;
    private List<SuccessRate> successRates;
    private AccessTrend accessTrend;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsageStat {
        private String name;
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessRate {
        private String name;
        private Integer success;
        private Integer fail;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessTrend {
        private List<String> dates;
        private List<Integer> counts;
    }
}
