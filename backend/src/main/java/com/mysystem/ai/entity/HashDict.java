package com.mysystem.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@TableName("hash_dict")
@Data
public class HashDict {

    private Long id;
    private Long userId;
    private Long arithmeticId;
    private String hashKey;
    private String hashValue;
    @TableField("`desc`")
    private String desc;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @TableField(exist = false)
    private Boolean autoGenerateHash;
}
