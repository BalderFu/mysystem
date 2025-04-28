package com.myhousekeepingservice.model.base;

import lombok.Data;

@Data
public class BaseStateCounter {
    private String name;
    private Integer state;
    private Integer count;
}
