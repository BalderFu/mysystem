package com.mysystem.ai.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**

 {
 "code": 200,
 "message": "请求成功",
 "data": {
 "usageStats": [
 {
 "name": "MD5",
 "value": 120
 },
 {
 "name": "AES",
 "success": 35,
 "fail": 65
 }
 ],
 "accessTrend": {
 "dates": ["3/22", "3/23", "3/24",...,"4/20"],
 "counts": [78, 65, 96,...,62]
 }
 }
 }


 */
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
