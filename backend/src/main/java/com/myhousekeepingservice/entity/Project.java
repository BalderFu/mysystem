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
    * 服务项目
    * </p>
*
* @author Jin
* @since 2025-04-25
*/
@Data
@TableName("`project`")
@ApiModel(value = "Project对象", description = "服务项目")
public class Project implements Serializable {

private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("家政")
    @TableField("`company_id`")
    private Long companyId;

    @ApiModelProperty("服务类型")
    @TableField("`service_id`")
    private Long serviceId;

    @ApiModelProperty("项目名")
    @TableField("`name`")
    private String name;

    @ApiModelProperty("图片")
    @TableField("`pic`")
    private String pic;

    @ApiModelProperty("价格")
    @TableField("`price`")
    private Long price;

    @ApiModelProperty("可约时间")
    @TableField("`time`")
    private String time;

    @ApiModelProperty("服务内容")
    @TableField("`content`")
    private String content;

    @ApiModelProperty("")
    @TableField("`create_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("")
    @TableField("`update_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime updateTime;

}
