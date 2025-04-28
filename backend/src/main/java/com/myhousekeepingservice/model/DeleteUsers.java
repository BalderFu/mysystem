package com.myhousekeepingservice.model;

import lombok.Data;

import java.util.List;

@Data
public class DeleteUsers {
    private List<Long> userIds;
}
