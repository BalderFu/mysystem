package com.myhousekeepingservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("`usage`")
public class Usage {
    private Long id;
    private Long arithmeticId;
    private String arithmeticName;
    private Boolean state;
    private Date createTime;
}
