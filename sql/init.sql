create database `jz_sensitive` charset 'utf8mb4';

CREATE TABLE `jz_sensitive`.`yonghu`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `role`        varchar(255) NOT NULL DEFAULT 'normal' COMMENT '角色',
    `username`    varchar(255) NOT NULL COMMENT '用户名',
    `password`    varchar(255) NOT NULL COMMENT '密码',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '用户表';


CREATE TABLE `jz_sensitive`.`minganci`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `keyword`        varchar(255) NOT NULL  COMMENT '关键词',
    `user_id`     BIGINT       NOT NULL COMMENT '用户',
    `description`    varchar(255) NOT NULL COMMENT '描述',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '敏感词';

CREATE TABLE `jz_sensitive`.`mingancilishijilu`
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `type`        varchar(255) NOT NULL  COMMENT '检测类型',
    `user_id`     BIGINT       NOT NULL COMMENT '用户',
    `source`      varchar(255) NOT NULL  COMMENT '来源',
    `origin`      longtext NOT NULL  COMMENT '原始内容',
    `content`     longtext NOT NULL  COMMENT '解析后的内容',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB COMMENT = '敏感词历史记录';

