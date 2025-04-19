package com.mysystem.ai.model;

import lombok.Data;

import java.util.List;

@Data
public class DeleteUsers {
    private List<Long> userIds;
}
