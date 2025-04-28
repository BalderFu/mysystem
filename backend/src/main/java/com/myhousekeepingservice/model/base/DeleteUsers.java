package com.myhousekeepingservice.model.base;

import lombok.Data;

import java.util.List;

@Data
public class DeleteUsers {
    private List<Long> userIds;
}
