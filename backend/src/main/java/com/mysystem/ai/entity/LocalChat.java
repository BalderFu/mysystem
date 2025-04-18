package com.mysystem.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("local_chat")
@Data
public class LocalChat {

    private Long id;
    private Long userId;
    private Long sessionId;
    private String ask;
    private String think;
    private String reply;
    private Date createTime;
}
