create database `MyCryptography` default character set 'utf8mb4';

CREATE TABLE `MyCryptography`.`user`
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

CREATE TABLE `MyCryptography`.`hash_arithmetic`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL  COMMENT '用户ID',
    `name`       varchar(1024) NOT NULL  COMMENT '名称',
    `keyword`        varchar(255) NOT NULL  COMMENT '算法名称',
    `description`    varchar(1024)   COMMENT '算法描述',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = 'hash算法';


CREATE TABLE `MyCryptography`.`hash_dict`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `arithmetic_id` BIGINT NOT NULL COMMENT '算法ID',
    `user_id` BIGINT NOT NULL, -- new add
    `hash_key`        varchar(1024) NOT NULL  COMMENT '',
    `hash_value`      varchar(1024) NOT NULL  COMMENT '',
    `desc`       varchar(1024) NOT NULL  COMMENT '描述',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '哈希字典';

CREATE TABLE `MyCryptography`.`log_operation`
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

CREATE TABLE `MyCryptography`.`usage`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `arithmetic_id`     BIGINT  NOT NULL  COMMENT '算法ID',
    `arithmetic_name`  varchar(64)   COMMENT '算法名称',
    `state` TINYINT  NOT NULL DEFAULT 1 COMMENT '成功还是失败',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '算法使用统计';

CREATE TABLE `MyCryptography`.`sys_access`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `count` INTEGER  NOT NULL DEFAULT 0 COMMENT '访问次数',
    `access_date` varchar(36)  NOT NULL  COMMENT '日期',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '系统访问量';


INSERT INTO `MyCryptography`.`user` ( `role`, `username`, `password`, `phone`, `email`,`nickname`) VALUES
    ('admin', 'admin', '123456', '13412345678',  '123456789@qq.com','管理员');


