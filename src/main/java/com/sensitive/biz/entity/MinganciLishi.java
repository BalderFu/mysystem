package com.sensitive.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mingancilishijilu")
public class MinganciLishi {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    @TableField(exist = false)
    private String username;
    private String type; // text、file
    private String source; // open api/ inner api
    private String origin; // 原始数据
    private String content; //解析后内容
    private Date createTime;
    private Date updateTime;
}
