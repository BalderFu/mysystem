package com.mysystem.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("phone_info")
@Data
public class PhoneInfo {
    private Long id;
    private String name;
    private Long userId;
    private String config;
    private String features;
    private Date createTime;
}
