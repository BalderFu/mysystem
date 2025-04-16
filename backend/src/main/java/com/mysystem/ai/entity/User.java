package com.mysystem.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
    private Date createTime;
}
