create database `my_housekeeping_service` default character set 'utf8mb4';

CREATE TABLE `my_housekeeping_service`.`user`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `role`        varchar(255) NOT NULL DEFAULT 'normal' COMMENT '角色',
    `username`    varchar(255) NOT NULL COMMENT '用户名',
    `password`    varchar(255) NOT NULL COMMENT '密码',
    `phone`    varchar(255) NOT NULL COMMENT '手机',
    `nickname`    varchar(255) NOT NULL COMMENT '昵称',
    `email`    varchar(255) NOT NULL COMMENT '邮箱',
    `avatar`    varchar(255)  COMMENT '头像',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '用户表';

CREATE TABLE `my_housekeeping_service`.`service_type`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`         varchar(36) NOT NULL  COMMENT '服务类型',
    `description`  varchar(36) NOT NULL  COMMENT '描述',
    `price`   decimal NOT NULL  COMMENT '价格',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '家政';

CREATE TABLE `my_housekeeping_service`.`company`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `state`   tinyint(1) NOT NULL  COMMENT '是否可以预约',
    `time`   varchar(36) NOT NULL  COMMENT '可约时间',
    `address`   varchar(1024) NOT NULL  COMMENT '地址',
    `name`   varchar(36) NOT NULL  COMMENT '名称',
    `phone`   varchar(36) NOT NULL  COMMENT '电话',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '家政';


CREATE TABLE `my_housekeeping_service`.`project`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`   BIGINT NOT NULL  COMMENT '家政',
    `service_id`   BIGINT NOT NULL  COMMENT '服务类型',
    `name` varchar(36) NOT NULL  COMMENT '项目名',
    `pic` varchar(1024) NOT NULL  COMMENT '图片',
    `price` decimal NOT NULL  COMMENT '价格',
    `time` varchar(36) NOT NULL  COMMENT '可约时间',
    `content` varchar(1024) NOT NULL  COMMENT '服务内容',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '服务项目';



CREATE TABLE `my_housekeeping_service`.`pre_order`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `project_id`   BIGINT NOT NULL  COMMENT '项目ID',
    `price`   decimal NOT NULL  COMMENT '价格',
    `time`   varchar(36) NOT NULL  COMMENT '预约时间',
    `address`   varchar(1024) NOT NULL  COMMENT '服务地址',
    `phone`   varchar(36) NOT NULL  COMMENT '手机号',
    `name`   varchar(36) NOT NULL  COMMENT '联系人电话',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '预定单';


CREATE TABLE `my_housekeeping_service`.`service_tempo`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `project_id`   BIGINT NOT NULL  COMMENT '项目ID',
    `state`   INTEGER NOT NULL  COMMENT '进度状态:1-上门中，2-服务中，3-服务完成',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '服务进度';


CREATE TABLE `my_housekeeping_service`.`comment`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL comment '用户ID',
    `pre_order_id` BIGINT       NOT NULL comment '订单ID',
    `content`   varchar(1024) NOT NULL  COMMENT '评论内容',
    `relay`   varchar(1024)   COMMENT '回复',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '评价';


CREATE TABLE `my_housekeeping_service`.`log_operation`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT  NOT NULL  COMMENT '用户ID',
    `username`  varchar(64)   COMMENT '用户名',
    `url`  varchar(1024)  NOT NULL  COMMENT '配置',
    `response`  text  NOT NULL  COMMENT '响应',
    `request`  text  DEFAULT NULL  COMMENT '请求',
    `used` INTEGER  NOT NULL  COMMENT '使用时长',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '操作记录';

CREATE TABLE `my_housekeeping_service`.`usage`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `arithmetic_id`     BIGINT  NOT NULL  COMMENT '算法ID',
    `arithmetic_name`  varchar(64)   COMMENT '算法名称',
    `state` TINYINT  NOT NULL DEFAULT 1 COMMENT '成功还是失败',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '算法使用统计';

CREATE TABLE `my_housekeeping_service`.`sys_access`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `count` INTEGER  NOT NULL DEFAULT 0 COMMENT '访问次数',
    `access_date` varchar(36)  NOT NULL  COMMENT '日期',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '系统访问量';


INSERT INTO `my_housekeeping_service`.`user` ( `role`, `username`, `password`, `phone`, `email`,`nickname`) VALUES
    ('admin', 'admin', '123456', '13412345678',  '123456789@qq.com','管理员');


