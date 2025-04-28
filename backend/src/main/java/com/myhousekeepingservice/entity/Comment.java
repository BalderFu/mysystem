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
 * 评价
 * </p>
 *
 * @author Jin
 * @since 2025-04-25
 */
@Data
@TableName("`comment`")
@ApiModel(value = "Comment对象", description = "评价")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    @TableField("`user_id`")
    private Long userId;

    @ApiModelProperty("订单ID")
    @TableField("`pre_order_id`")
    private Long preOrderId;

    @ApiModelProperty("评论内容")
    @TableField("`content`")
    private String content;

    @ApiModelProperty("回复")
    @TableField("`relay`")
    private String relay;

    @ApiModelProperty("")
    @TableField("`create_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("")
    @TableField("`update_time`")
    @JsonFormat(pattern = "yyyy-MM-dd HH::mm:ss")
    private LocalDateTime updateTime;

}
