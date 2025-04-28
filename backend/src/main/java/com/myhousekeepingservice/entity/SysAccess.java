package com.myhousekeepingservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("sys_access")
@Data
public class SysAccess {
    private Long id;
    private Integer count;
    private String accessDate;
    private Date createTime;
}
