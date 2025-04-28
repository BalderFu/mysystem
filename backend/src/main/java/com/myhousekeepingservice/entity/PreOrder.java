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
    * 预定单
    * </p>
*
* @author Jin
* @since 2025-04-25
*/
@Data
@TableName("`pre_order`")
@ApiModel(value = "PreOrder对象", description = "预定单")
public class PreOrder implements Serializable {

private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("项目ID")
    @TableField("`project_id`")
    private Long projectId;

    @ApiModelProperty("价格")
    @TableField("`price`")
    private Long price;

    @ApiModelProperty("预约时间")
    @TableField("`time`")
    private String time;

    @ApiModelProperty("服务地址")
    @TableField("`address`")
    private String address;

    @ApiModelProperty("手机号")
    @TableField("`phone`")
    private String phone;

    @ApiModelProperty("联系人电话")
    @TableField("`name`")
    private String name;

    @ApiModelProperty("")
    @TableField("`create_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("")
    @TableField("`update_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime updateTime;

}
