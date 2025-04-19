create database `my_system` default character set 'utf8mb4';

CREATE TABLE `my_system`.`user`
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

CREATE TABLE `my_system`.`history_session`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `title`        varchar(255) NOT NULL DEFAULT 'normal' COMMENT '角色',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '会话';


CREATE TABLE `my_system`.`local_chat`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT  NOT NULL  COMMENT '用户',
    `session_id`  BIGINT  NOT NULL  COMMENT '会话',
    `ask`  text  NOT NULL  COMMENT '询问',
    `think`  text  NOT NULL  COMMENT '思考',
    `reply`  longtext  NOT NULL  COMMENT '应答',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '聊天';


CREATE TABLE `my_system`.`phone_info`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT  NOT NULL  COMMENT '用户',
    `name`  varchar(64)  NOT NULL  COMMENT '手机名',
    `config`  varchar(1024)  NOT NULL  COMMENT '配置',
    `features`  varchar(2048)  NOT NULL  COMMENT '特性',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '手机号';


CREATE TABLE `my_system`.`log_operation`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT  NOT NULL  COMMENT '用户ID',
    `username`  varchar(64)   COMMENT '用户名',
    `url`  varchar(1024)  NOT NULL  COMMENT '配置',
    `response`  text  NOT NULL  COMMENT '响应',
    `request`  text  NOT NULL  COMMENT '请求',
    `used` INTEGER  NOT NULL  COMMENT '使用时长',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '操作记录';


INSERT INTO `my_system`.`user` ( `role`, `username`, `password`, `phone`, `email`,`nickname`) VALUES
('admin', 'admin', '123456', '13412345678',  '123456789@qq.com','管理员');


