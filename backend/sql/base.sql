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

CREATE TABLE `my_housekeeping_service`.`sys_access`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `count` INTEGER  NOT NULL DEFAULT 0 COMMENT '访问次数',
    `access_date` varchar(36)  NOT NULL  COMMENT '日期',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '系统访问量';


INSERT INTO `my_housekeeping_service`.`user` ( `role`, `username`, `password`, `phone`, `email`,`nickname`) VALUES
    ('admin', 'admin', '123456', '13412345678',  '123456789@qq.com','管理员');
