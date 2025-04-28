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
import java.math.BigDecimal;
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
@TableName("`service_type`")
@ApiModel(value = "ServiceType对象", description = "家政")
public class ServiceType implements Serializable {

private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("服务类型")
    @TableField("`name`")
    private String name;

    @ApiModelProperty("描述")
    @TableField("`description`")
    private String description;

    @ApiModelProperty("")
    @TableField("`create_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("")
    @TableField("`update_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime updateTime;

    @ApiModelProperty("")
    @TableField("`price`")
    private BigDecimal price;

}
