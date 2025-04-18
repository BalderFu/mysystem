package com.mysystem.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("history_session")
@Data
public class HistorySession {
    private Long id;
    private String title;
    private Date createTime;

}
