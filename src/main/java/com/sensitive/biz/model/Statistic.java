package com.sensitive.biz.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class Statistic {

    private int nums;
    private Set<String> words;
    private String content;
}
