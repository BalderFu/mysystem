package com.mysystem.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@TableName("log_operation")
@Data
public class LogOperation {
   private Long id;
   private Long userId;
   private String username;
   private String url;
   private String response;
   private String request;
   private Long used;
   @JsonFormat(pattern = "yyyy-MM-dd hh::mm:ss")
   private Date createTime;
}
