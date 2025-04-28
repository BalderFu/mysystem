package com.myhousekeepingservice.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateStatistic {

    private String access_date;
    private Integer total_access_count;
}
