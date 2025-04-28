package com.myhousekeepingservice.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatistic {
    private Long arithmetic_id;
    private String arithmetic_name;
    private Integer success_count;
    private Integer failure_count;
}
