/*
 Navicat MySQL Data Transfer

 Source Server         : MySql
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : localhost:3306
 Source Schema         : kchat

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 07/03/2025 23:02:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chatmessage
-- ----------------------------
DROP TABLE IF EXISTS `chatmessage`;
CREATE TABLE `chatmessage`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '聊天信息id,自增',
  `userId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送信息的用户id',
  `contactId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接受消息的对象id',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '消息类型，1.普通文本，2.图片，3.文件,4.验证信息',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息内容',
  `fileName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名',
  `fileSize` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `read` tinyint(1) NULL DEFAULT NULL COMMENT '是否已读',
  `time` datetime NULL DEFAULT NULL COMMENT '消息收到/成功发送的时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_userId_contactId`(`userId`, `contactId`) USING BTREE,
  INDEX `index_type`(`type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chatmessage
-- ----------------------------

-- ----------------------------
-- Table structure for contactlabel
-- ----------------------------
DROP TABLE IF EXISTS `contactlabel`;
CREATE TABLE `contactlabel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '联系人标签id,自增',
  `name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签名称',
  `userId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `index_userId_name`(`userId`, `name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contactlabel
-- ----------------------------

-- ----------------------------
-- Table structure for groupnotice
-- ----------------------------
DROP TABLE IF EXISTS `groupnotice`;
CREATE TABLE `groupnotice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '群公告id，自增',
  `groupId` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群聊id',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_groupId`(`groupId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of groupnotice
-- ----------------------------

-- ----------------------------
-- Table structure for headmessage
-- ----------------------------
DROP TABLE IF EXISTS `headmessage`;
CREATE TABLE `headmessage`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '头信息id,自增',
  `content` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '展示内容',
  `userId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户Id',
  `contactId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息联系对象id',
  `isVerification` tinyint(1) NULL DEFAULT NULL COMMENT '是否属于验证消息',
  `time` datetime NULL DEFAULT NULL COMMENT '最后一次聊天的时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_userId`(`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of headmessage
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户id',
  `account` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户账号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户电子邮箱',
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别，1：男，0：女',
  `password` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `avatar` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'default.png' COMMENT '用户头像',
  `signature` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个性签名',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '用户状态，1：正常，0：异常',
  `createTime` datetime NULL DEFAULT NULL COMMENT '用户创建时间',
  `lastLoginTime` datetime NULL DEFAULT NULL COMMENT '上一次登录时间',
  `area` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户所属地区',
  `areaCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户所属地区的地区代码',
  `lastOfflineTime` bigint NULL DEFAULT NULL COMMENT '上一次离线时间',
  `role` tinyint NULL DEFAULT 1 COMMENT '用户角色,1.管理员，2.普通用户',
  `offline` tinyint(1) NULL DEFAULT NULL COMMENT '用户是否离线，0：在线，1：离线',
  `acceptMode` tinyint(1) NULL DEFAULT 1 COMMENT '添加好友方式：1.需要申请 0.直接可被加为好友',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE,
  UNIQUE INDEX `index_account`(`account`) USING BTREE,
  INDEX `index_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('U4420160259', '442016025985', '1816440933@qq.com', 'sasuke', 0, '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'default-user.png', NULL, 1, '2025-03-06 16:40:26', '2025-03-06 16:40:42', NULL, NULL, NULL, 1, NULL, 1);
INSERT INTO `user` VALUES ('U44314880901235', '643148809012', '2504319659@qq.com', 'yolu', 0, '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'default-user.png', NULL, 1, '2025-03-01 15:32:32', '2025-03-03 17:46:31', NULL, NULL, NULL, NULL, NULL, 1);

-- ----------------------------
-- Table structure for userapply
-- ----------------------------
DROP TABLE IF EXISTS `userapply`;
CREATE TABLE `userapply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户申请信息维护表id,自增',
  `userId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `contactId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系对象id',
  `info` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '验证信息',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '申请状态，1.验证中，2.忽略，3.拒绝',
  `time` datetime NULL DEFAULT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_status`(`status`) USING BTREE,
  INDEX `index_userId_contactId`(`userId`, `contactId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of userapply
-- ----------------------------

-- ----------------------------
-- Table structure for usercontact
-- ----------------------------
DROP TABLE IF EXISTS `usercontact`;
CREATE TABLE `usercontact`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户联系表id,自增',
  `userId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户id',
  `contactId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系对象id',
  `remark` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '联系人状态，1.正常，2.拉黑，3.删除，4.被拉黑，5.被删除',
  `labelId` bigint NULL DEFAULT NULL COMMENT '联系人标签',
  `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isGroup` tinyint(1) NULL DEFAULT NULL COMMENT '是否是对组的联系',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `index_userId_contactId`(`userId`, `contactId`) USING BTREE,
  INDEX `index_labelId`(`labelId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of usercontact
-- ----------------------------

-- ----------------------------
-- Table structure for usergroup
-- ----------------------------
DROP TABLE IF EXISTS `usergroup`;
CREATE TABLE `usergroup`  (
  `id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '群聊主键',
  `name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群名称',
  `avatar` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群头像',
  `ownerId` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群主id',
  `currentCount` int NULL DEFAULT 0 COMMENT '群当前人数',
  `size` tinyint(1) NULL DEFAULT NULL COMMENT '群规模',
  `acceptMode` tinyint(1) NULL DEFAULT NULL COMMENT '进群接受模式，1.无需申请，直接进入，2.需要申请',
  `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_ownerId`(`ownerId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of usergroup
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
