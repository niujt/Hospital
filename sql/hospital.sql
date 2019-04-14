/*
 Navicat Premium Data Transfer

 Source Server         : MyDB
 Source Server Type    : MySQL
 Source Server Version : 50712
 Source Host           : localhost:3306
 Source Schema         : hospital

 Target Server Type    : MySQL
 Target Server Version : 50712
 File Encoding         : 65001

 Date: 14/04/2019 16:23:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `patientid` int(11) NULL DEFAULT NULL COMMENT '患者id',
                              `doctorid` int(11) NULL DEFAULT NULL COMMENT '医生id',
                              `time` date NULL DEFAULT NULL COMMENT '预约时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (1, 1, 2, '2017-03-07');
INSERT INTO `appointment` VALUES (7, 2, 2, '2018-11-27');
INSERT INTO `appointment` VALUES (8, 3, 2, '2018-12-01');
INSERT INTO `appointment` VALUES (9, 4, 1, '2023-02-01');
INSERT INTO `appointment` VALUES (10, 4, 2, '2019-03-05');
INSERT INTO `appointment` VALUES (13, 3, 1, '2019-03-31');
INSERT INTO `appointment` VALUES (14, 4, 1, '2017-02-05');
INSERT INTO `appointment` VALUES (15, 1, 1, '2019-04-06');
INSERT INTO `appointment` VALUES (16, 1, 2, '2019-04-27');

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor`  (
                         `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
                         `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
                         `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
                         `certId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证',
                         `sex` int(10) NULL DEFAULT NULL COMMENT '性别',
                         `department` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门',
                         `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '家庭住址',
                         `loginid` int(11) NULL DEFAULT NULL COMMENT '登录',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES (1, '张三三', 32, '666', 1, '精神科', '江苏/苏州', 3);
INSERT INTO `doctor` VALUES (2, '李四', 32, '777', 1, '妇产科', '江苏/无锡', 4);
INSERT INTO `doctor` VALUES (3, '王五', 56, '888', 1, '妇产科', 'sasasas', 12);

-- ----------------------------
-- Table structure for drugs
-- ----------------------------
DROP TABLE IF EXISTS `drugs`;
CREATE TABLE `drugs`  (
                        `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '药品名称',
                        `type` int(10) NULL DEFAULT NULL COMMENT '药品类型',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of drugs
-- ----------------------------
INSERT INTO `drugs` VALUES (1, '111111', 4);
INSERT INTO `drugs` VALUES (2, '22', 1);
INSERT INTO `drugs` VALUES (3, '33', 2);
INSERT INTO `drugs` VALUES (4, '44', 3);
INSERT INTO `drugs` VALUES (5, '55', 4);
INSERT INTO `drugs` VALUES (7, '66', 5);
INSERT INTO `drugs` VALUES (13, 'werwer', 2);

-- ----------------------------
-- Table structure for hospitalization
-- ----------------------------
DROP TABLE IF EXISTS `hospitalization`;
CREATE TABLE `hospitalization`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `floor` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '楼层',
                                  `bed` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '床号',
                                  `door` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '房间号',
                                  `medicalname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '病名称',
                                  `patientid` int(11) NULL DEFAULT NULL COMMENT '患者id',
                                  `intime` date NULL DEFAULT NULL COMMENT '住院时间',
                                  `outtime` date NULL DEFAULT NULL COMMENT '出院时间',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hospitalization
-- ----------------------------
INSERT INTO `hospitalization` VALUES (1, '3', '4', '304', 'qweq', 1, '2019-03-07', '2019-03-08');
INSERT INTO `hospitalization` VALUES (2, '2', '3', '305', 'asd', 2, '2019-03-07', '2019-03-10');
INSERT INTO `hospitalization` VALUES (3, '2', '2', '305', 'asd', 3, '2019-03-07', '2019-03-10');
INSERT INTO `hospitalization` VALUES (4, '2', '1', '305', 'dsad', 4, '2019-03-07', '2019-03-18');
INSERT INTO `hospitalization` VALUES (5, '3', '2', '306', 'add', 4, '2019-03-07', '2019-03-19');
INSERT INTO `hospitalization` VALUES (8, '1', '2', '102', 'adasdasd', 3, '2019-03-28', '2019-03-31');
INSERT INTO `hospitalization` VALUES (9, '1', '1', '102', '脑残', 1, '2019-04-06', NULL);

-- ----------------------------
-- Table structure for login
-- ----------------------------
DROP TABLE IF EXISTS `login`;
CREATE TABLE `login`  (
                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
                        `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
                        `role` int(255) NULL DEFAULT NULL COMMENT '权限',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login
-- ----------------------------
INSERT INTO `login` VALUES (1, 'admin1', '123456', 1);
INSERT INTO `login` VALUES (2, 'admin2', '123456', 1);
INSERT INTO `login` VALUES (3, 'zhangsan', '123456', 2);
INSERT INTO `login` VALUES (4, 'lisi', '123456', 2);
INSERT INTO `login` VALUES (5, 'zhaoone', '123456', 3);
INSERT INTO `login` VALUES (6, 'zhaotwo', '123456', 3);
INSERT INTO `login` VALUES (7, 'zhaothree', '123456', 3);
INSERT INTO `login` VALUES (8, 'zhaofour', '123456', 3);
INSERT INTO `login` VALUES (9, 'zhaofive', '123456', 3);
INSERT INTO `login` VALUES (10, 'admin3', '123456', 1);
INSERT INTO `login` VALUES (11, 'admin4', '123456', 1);
INSERT INTO `login` VALUES (12, 'wangwu', '123456', 2);
INSERT INTO `login` VALUES (13, '111', '123456', 1);

-- ----------------------------
-- Table structure for medicalhistory
-- ----------------------------
DROP TABLE IF EXISTS `medicalhistory`;
CREATE TABLE `medicalhistory`  (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `patientid` int(10) NULL DEFAULT NULL COMMENT '患者id',
                                 `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '病史名称',
                                 `time` date NULL DEFAULT NULL COMMENT '患病时间',
                                 `hospitalizationid` int(11) NULL DEFAULT NULL COMMENT '住院信息',
                                 `doctorid` int(11) NULL DEFAULT NULL COMMENT '确诊人',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medicalhistory
-- ----------------------------
INSERT INTO `medicalhistory` VALUES (1, 1, '狂犬病', '2019-03-07', 1, 1);
INSERT INTO `medicalhistory` VALUES (2, 2, '狂犬病', '2019-03-07', 2, 1);
INSERT INTO `medicalhistory` VALUES (3, 3, '狂犬病', '2019-03-07', 3, 2);
INSERT INTO `medicalhistory` VALUES (4, 4, '狂犬病', '2019-03-07', 4, 2);
INSERT INTO `medicalhistory` VALUES (5, 1, '狂犬病', '2019-03-23', 1, 2);

-- ----------------------------
-- Table structure for patient
-- ----------------------------
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient`  (
                          `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
                          `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
                          `age` int(10) NULL DEFAULT NULL COMMENT '年龄',
                          `certId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证',
                          `sex` int(10) NULL DEFAULT NULL COMMENT '性别',
                          `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '家庭住址',
                          `Hospitalizationid` int(10) NULL DEFAULT NULL COMMENT '住院信息',
                          `drugsids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '药品信息',
                          `isout` int(255) NULL DEFAULT NULL COMMENT '是否出院',
                          `appointmentid` int(11) NULL DEFAULT NULL COMMENT '预约信息',
                          `loginid` int(11) NULL DEFAULT NULL COMMENT '登录',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of patient
-- ----------------------------
INSERT INTO `patient` VALUES (1, '赵老一', 20, '111', 0, '江苏/苏州', 1, '1,2,3,4', 0, 15, 5);
INSERT INTO `patient` VALUES (2, '赵老二', 21, '222', 0, '江苏/无锡', 2, '2', 0, 7, 6);
INSERT INTO `patient` VALUES (3, '赵老三', 22, '333', 0, '江苏/淮安', 3, '3', 1, 8, 7);
INSERT INTO `patient` VALUES (4, '赵老四', 23, '444', 0, '江苏/镇江', 4, '4', 2, 9, 8);
INSERT INTO `patient` VALUES (5, '赵老五', 32, '555', 1, '江苏/徐州', 1, '2', 1, 9, 9);

SET FOREIGN_KEY_CHECKS = 1;
