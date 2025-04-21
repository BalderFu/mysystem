package com.mysystem.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * //算法类型，digest-信息摘要算法，asymmetrical-对称解密，unsymmetrical-非对称加密，hybrid
 */
@TableName("hash_arithmetic")
@Data
public class HashArithmetic {
    private Long id;
    private Long userId;
    private String name;
    private String keyword;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;
    private String description; // 算法描述信息
}
