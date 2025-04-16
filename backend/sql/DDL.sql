create database `my_system` default character set 'utf8mb4';

CREATE TABLE `my_system`.`user`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `role`        varchar(255) NOT NULL DEFAULT 'normal' COMMENT '角色',
    `username`    varchar(255) NOT NULL COMMENT '用户名',
    `password`    varchar(255) NOT NULL COMMENT '密码',
    `phone`    varchar(255) NOT NULL COMMENT '手机',
   -- `avatar`    varchar(255) NOT NULL COMMENT '头像',
    `email`    varchar(255) NOT NULL COMMENT '邮箱',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '用户表';



