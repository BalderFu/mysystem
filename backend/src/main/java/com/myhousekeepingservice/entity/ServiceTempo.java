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
    * 服务进度
    * </p>
*
* @author Jin
* @since 2025-04-25
*/
@Data
@TableName("`service_tempo`")
@ApiModel(value = "ServiceTempo对象", description = "服务进度")
public class ServiceTempo implements Serializable {

private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("项目ID")
    @TableField("`project_id`")
    private Long projectId;

    @ApiModelProperty("进度状态:1-上门中，2-服务中，3-服务完成")
    @TableField("`state`")
    private Integer state;

    @ApiModelProperty("")
    @TableField("`create_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("")
    @TableField("`update_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime updateTime;

}
