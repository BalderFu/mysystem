package com.mysystem.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@TableName("user")
@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;
    private String role; // admin、normal
    private String avatar;
    @JsonFormat(pattern = "yyyy-MM-dd hh::mm:ss")
    private Date createTime;
}
