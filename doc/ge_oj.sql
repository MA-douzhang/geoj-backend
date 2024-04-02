/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80029 (8.0.29)
 Source Host           : localhost:3306
 Source Schema         : ge_oj

 Target Server Type    : MySQL
 Target Server Version : 80029 (8.0.29)
 File Encoding         : 65001

 Date: 28/03/2024 16:22:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

create database ge_oj;
use ge_oj;
-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '消息id',
    `senderId`    bigint                                                        NOT NULL COMMENT '发送者id',
    `receiverId`  bigint                                                        NOT NULL COMMENT '接收者id',
    `content`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '信息体(最大200字)',
    `contentType` int                                                           NOT NULL COMMENT '信息类型，1为评论，2为回复评论，3为点赞，4系统通知',
    `targetId`    bigint                                                        NULL     DEFAULT NULL COMMENT '信息体对象，帖子id等',
    `noticeState` tinyint                                                       NOT NULL DEFAULT 0 COMMENT '状态 0未读，1已读',
    `createTime`  datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`  timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`    tinyint                                                       NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`
(
    `id`         bigint                                                         NOT NULL AUTO_INCREMENT COMMENT 'id',
    `title`      varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '标题',
    `content`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NULL COMMENT '内容',
    `tags`       varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '标签列表（json 数组）',
    `thumbNum`   int                                                            NOT NULL DEFAULT 0 COMMENT '点赞数',
    `favourNum`  int                                                            NOT NULL DEFAULT 0 COMMENT '收藏数',
    `userId`     bigint                                                         NOT NULL COMMENT '创建用户 id',
    `questionId` bigint                                                         NOT NULL COMMENT '题目id',
    `createTime` datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`   tinyint                                                        NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_userId` (`userId` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1758687464531136514
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '帖子'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_comment
-- ----------------------------
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment`
(
    `id`           bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '评论id',
    `userId`       bigint                                                        NOT NULL COMMENT '评论用户id',
    `postId`       bigint                                                        NOT NULL COMMENT '评论帖子id',
    `content`      varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容(最大200字)',
    `pid`          bigint                                                        NOT NULL COMMENT '父id',
    `commentState` int                                                           NOT NULL DEFAULT 0 COMMENT '状态 0 正常',
    `thumbNum`     int                                                           NOT NULL DEFAULT 0 COMMENT '点赞数',
    `createTime`   datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`   timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`     tinyint                                                       NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_favour
-- ----------------------------
DROP TABLE IF EXISTS `post_favour`;
CREATE TABLE `post_favour`
(
    `id`         bigint   NOT NULL AUTO_INCREMENT COMMENT 'id',
    `postId`     bigint   NOT NULL COMMENT '帖子 id',
    `userId`     bigint   NOT NULL COMMENT '创建用户 id',
    `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_postId` (`postId` ASC) USING BTREE,
    INDEX `idx_userId` (`userId` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子收藏'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_thumb
-- ----------------------------
DROP TABLE IF EXISTS `post_thumb`;
CREATE TABLE `post_thumb`
(
    `id`         bigint   NOT NULL AUTO_INCREMENT COMMENT 'id',
    `postId`     bigint   NOT NULL COMMENT '帖子 id',
    `userId`     bigint   NOT NULL COMMENT '创建用户 id',
    `type`       int      NOT NULL DEFAULT 0 COMMENT '默认0为post点赞 1为postCommon点赞',
    `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_postId` (`postId` ASC) USING BTREE,
    INDEX `idx_userId` (`userId` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 25
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子点赞'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`
(
    `id`          bigint                                                         NOT NULL AUTO_INCREMENT COMMENT 'id',
    `title`       varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '标题',
    `content`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NULL COMMENT '内容',
    `tags`        varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '标签列表（json 数组）',
    `answer`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NULL COMMENT '题目答案',
    `submitNum`   int                                                            NOT NULL DEFAULT 0 COMMENT '题目提交数',
    `acceptedNum` int                                                            NOT NULL DEFAULT 0 COMMENT '题目通过数',
    `judgeCase`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NULL COMMENT '判题用例（json 数组）',
    `judgeConfig` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NULL COMMENT '判题配置（json 对象）',
    `thumbNum`    int                                                            NOT NULL DEFAULT 0 COMMENT '点赞数',
    `favourNum`   int                                                            NOT NULL DEFAULT 0 COMMENT '收藏数',
    `userId`      bigint                                                         NOT NULL COMMENT '创建用户 id',
    `difficulty`  int                                                            NOT NULL DEFAULT 1 COMMENT '题目难度 1-简单 2-中等 3-困难',
    `createTime`  datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`  datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`    tinyint                                                        NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_userId` (`userId` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1745756131455123458
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '题目'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_submit
-- ----------------------------
DROP TABLE IF EXISTS `question_submit`;
CREATE TABLE `question_submit`
(
    `id`         bigint                                                        NOT NULL AUTO_INCREMENT COMMENT 'id',
    `language`   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '编程语言',
    `code`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NOT NULL COMMENT '用户代码',
    `judgeInfo`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '判题信息（json 对象）',
    `status`     int                                                           NOT NULL DEFAULT 0 COMMENT '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    `questionId` bigint                                                        NOT NULL COMMENT '题目 id',
    `userId`     bigint                                                        NOT NULL COMMENT '创建用户 id',
    `createTime` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`   tinyint                                                       NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_questionId` (`questionId` ASC) USING BTREE,
    INDEX `idx_userId` (`userId` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1763937728033374211
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目提交'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`           bigint                                                         NOT NULL AUTO_INCREMENT COMMENT 'id',
    `userAccount`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '账号',
    `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '密码',
    `unionId`      varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '微信开放平台id',
    `mpOpenId`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '公众号openId',
    `userName`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '用户昵称',
    `userAvatar`   varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '用户头像',
    `userProfile`  varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '用户简介',
    `userRole`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
    `createTime`   datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`   datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`     tinyint                                                        NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_unionId` (`unionId` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1720816432361955330
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户'
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

use ge_oj;
/**
  竞赛表
 */
create table if not exists game
(
    id           bigint auto_increment comment 'id' primary key,
    createUserId bigint                             not null comment '创建用户 id',
    gameTitle    varchar(256)                       not null comment '竞赛标题',
    gameProfile  varchar(512)                       null comment '赛事介绍',
    gameTotalNum int      default 1                 not null comment '竞赛限制人数',
    gameType     varchar(64)                        not null comment '竞赛类型',
    publicType   varchar(64)                        not null comment '公开类型',
    startTime    datetime                           not null comment '开启时间',
    endTime      datetime                           not null comment '结束时间',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
) comment '竞赛表' collate = utf8mb4_unicode_ci;

/**
  竞赛题目关系表
 */
create table if not exists game_question
(
    id            bigint auto_increment comment 'id' primary key,
    gameId        bigint not null comment '竞赛 id',
    questionId    bigint not null comment '题目 id',
    questionScore int    not null comment '题目积分'
) comment '竞赛题目关系表' collate = utf8mb4_unicode_ci;

/**
  竞赛用户关系表
 */
create table if not exists game_user
(
    id         bigint auto_increment comment 'id' primary key,
    gameId     bigint                             not null comment '竞赛 id',
    userId     bigint                             not null comment '用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '竞赛用户关系表' collate = utf8mb4_unicode_ci;

/**
  竞赛排名关系表
 */
create table if not exists game_rank
(
    id          bigint auto_increment comment 'id' primary key,
    gameId      bigint                             not null comment '竞赛 id',
    userId      bigint                             not null comment '用户 id',
    totalMemory int                                null comment '总空间',
    totalTime   int                                null comment '总空间',
    totalScore  int                                null comment '总空间',
    gameDetail  text comment '竞赛详情',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
) comment '竞赛排名关系表' collate = utf8mb4_unicode_ci;
