package com.myhousekeepingservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 家政
    * </p>
*
* @author Jin
* @since 2025-04-25
*/
@Data
@TableName("`company`")
@ApiModel(value = "Company对象", description = "家政")
public class Company implements Serializable {

private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("是否可以预约")
    @TableField("`state`")
    private Boolean state;

    @ApiModelProperty("可约时间")
    @TableField("`time`")
    private String time;

    @ApiModelProperty("地址")
    @TableField("`address`")
    private String address;

    @ApiModelProperty("名称")
    @TableField("`name`")
    private String name;

    @ApiModelProperty("电话")
    @TableField("`phone`")
    private String phone;

    @ApiModelProperty("")
    @TableField("`create_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("")
    @TableField("`update_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime updateTime;

}
