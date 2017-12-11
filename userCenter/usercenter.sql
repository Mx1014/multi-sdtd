/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : usercenter

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-11-10 10:44:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `rztmenuprivilege`
-- ----------------------------
DROP TABLE IF EXISTS `rztmenuprivilege`;
CREATE TABLE `rztmenuprivilege` (
  `id` varchar(255) NOT NULL,
  `menuId` varchar(255) DEFAULT NULL,
  `operateId` varchar(255) DEFAULT NULL,
  `roleid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztmenuprivilege
-- ----------------------------
INSERT INTO `rztmenuprivilege` VALUES ('402881075f9a5db4015f9a602b900002', '402881075f99363d015f99406d220000', null, '402881075f766bbc015f7670f77a0000');
INSERT INTO `rztmenuprivilege` VALUES ('402881075f9a5db4015f9a602b940003', '402881075f80a337015f80a54f660000', null, '402881075f766bbc015f7670f77a0000');
INSERT INTO `rztmenuprivilege` VALUES ('402881075f9f7839015f9f7eaabc0001', '402881075f80a337015f80a54f660000', null, '402881075f764f94015f7655c4f10000');
INSERT INTO `rztmenuprivilege` VALUES ('402881075f9f7839015f9f7eaabe0002', '402881075f99363d015f99406d220000', null, '402881075f764f94015f7655c4f10000');

-- ----------------------------
-- Table structure for `rztsysdepartment`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysdepartment`;
CREATE TABLE `rztsysdepartment` (
  `id` varchar(255) NOT NULL,
  `createtime` datetime DEFAULT NULL,
  `deptDesc` varchar(255) DEFAULT NULL,
  `deptIcon` varchar(255) DEFAULT NULL,
  `deptName` varchar(255) DEFAULT NULL,
  `deptPid` varchar(255) DEFAULT NULL,
  `lft` int(11) DEFAULT NULL,
  `rgt` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysdepartment
-- ----------------------------
INSERT INTO `rztsysdepartment` VALUES ('402881075f9f7839015f9f7ce7c20000', '2017-11-09 14:34:43', null, null, '花乡供电所', 'a0b48f11bd5911e79a2668f728344c35', '18', '19');
INSERT INTO `rztsysdepartment` VALUES ('402881075f9f7839015f9f81a6160003', '2017-11-09 14:39:55', null, null, '和义供电所', 'a0b48f11bd5911e79a2668f728344c35', '16', '17');
INSERT INTO `rztsysdepartment` VALUES ('402881075f9f7839015f9f82bad20004', '2017-11-09 14:41:06', null, null, '马家堡供电所', 'a0b48f11bd5911e79a2668f728344c35', '14', '15');
INSERT INTO `rztsysdepartment` VALUES ('402881075f9f7839015f9f82e4e70005', '2017-11-09 14:41:16', null, null, '六里桥供电所', 'a0b48f11bd5911e79a2668f728344c35', '12', '13');
INSERT INTO `rztsysdepartment` VALUES ('402881075f9f7839015f9f83074a0006', '2017-11-09 14:41:25', null, null, '方庄供电所', 'a0b48f11bd5911e79a2668f728344c35', '10', '11');
INSERT INTO `rztsysdepartment` VALUES ('402881075f9f7839015f9f832a560007', '2017-11-09 14:41:34', null, null, '云冈供电所', 'a0b48f11bd5911e79a2668f728344c35', '8', '9');
INSERT INTO `rztsysdepartment` VALUES ('402881075f9f7839015f9f834fbd0008', '2017-11-09 14:41:44', null, null, '科技园供电所', 'a0b48f11bd5911e79a2668f728344c35', '6', '7');
INSERT INTO `rztsysdepartment` VALUES ('402881075f9f7839015f9f836fcf0009', '2017-11-09 14:41:52', null, null, '右安门供电所', 'a0b48f11bd5911e79a2668f728344c35', '4', '5');
INSERT INTO `rztsysdepartment` VALUES ('402881075f9f7839015f9f839428000a', '2017-11-09 14:42:01', null, null, '南苑供电所', 'a0b48f11bd5911e79a2668f728344c35', '2', '3');
INSERT INTO `rztsysdepartment` VALUES ('a0b48f11bd5911e79a2668f728344c35', '2017-10-30 18:04:14', '丰台供电公司', null, '丰台供电公司', '0', '1', '20');

-- ----------------------------
-- Table structure for `rztsysgroup`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysgroup`;
CREATE TABLE `rztsysgroup` (
  `id` varchar(255) NOT NULL,
  `createtime` datetime DEFAULT NULL,
  `groupDesc` varchar(255) DEFAULT NULL,
  `groupIcon` varchar(255) DEFAULT NULL,
  `groupName` varchar(255) DEFAULT NULL,
  `groupPid` varchar(255) DEFAULT NULL,
  `lft` int(11) DEFAULT NULL,
  `rgt` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysgroup
-- ----------------------------

-- ----------------------------
-- Table structure for `rztsysgrouprole`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysgrouprole`;
CREATE TABLE `rztsysgrouprole` (
  `id` varchar(255) NOT NULL,
  `groupId` varchar(255) DEFAULT NULL,
  `roleId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysgrouprole
-- ----------------------------

-- ----------------------------
-- Table structure for `rztsysmenu`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysmenu`;
CREATE TABLE `rztsysmenu` (
  `id` varchar(255) NOT NULL,
  `createtime` datetime DEFAULT NULL,
  `lft` int(11) DEFAULT NULL,
  `menuDesc` varchar(255) DEFAULT NULL,
  `menuIcon` varchar(255) DEFAULT NULL,
  `menuName` varchar(255) DEFAULT NULL,
  `menuPid` varchar(255) DEFAULT NULL,
  `menuUrl` varchar(255) DEFAULT NULL,
  `rgt` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysmenu
-- ----------------------------
INSERT INTO `rztsysmenu` VALUES ('402881075f80a337015f80a54f660000', '2017-11-03 14:50:38', '4', null, null, '数据维护', '62783cb6bd4911e79a2668f728344c35', '111', '5');
INSERT INTO `rztsysmenu` VALUES ('402881075f99363d015f99406d220000', '2017-11-08 09:30:57', '2', null, null, '工单合同管理', '62783cb6bd4911e79a2668f728344c35', 'sdsads', '3');
INSERT INTO `rztsysmenu` VALUES ('62783cb6bd4911e79a2668f728344c35', '2017-10-30 16:08:00', '1', '菜单管理', null, '菜单管理', '0', null, '6');

-- ----------------------------
-- Table structure for `rztsysoperate`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysoperate`;
CREATE TABLE `rztsysoperate` (
  `id` varchar(255) NOT NULL,
  `operateName` varchar(255) DEFAULT NULL,
  `operateNum` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysoperate
-- ----------------------------

-- ----------------------------
-- Table structure for `rztsysrole`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysrole`;
CREATE TABLE `rztsysrole` (
  `id` varchar(255) NOT NULL,
  `createtime` datetime DEFAULT NULL,
  `roleDesc` varchar(255) DEFAULT NULL,
  `roleName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysrole
-- ----------------------------
INSERT INTO `rztsysrole` VALUES ('402881075f764f94015f7655c4f10000', '2017-11-01 14:47:33', '范德萨范德萨', '发射点犯得上');
INSERT INTO `rztsysrole` VALUES ('402881075f764f94015f765b46e10001', '2017-11-01 14:53:16', null, '发射点犯得上');
INSERT INTO `rztsysrole` VALUES ('402881075f764f94015f765c21660002', '2017-11-01 14:54:26', null, '大大撒');
INSERT INTO `rztsysrole` VALUES ('402881075f764f94015f765ec8480003', '2017-11-01 14:57:24', null, '是非得失');
INSERT INTO `rztsysrole` VALUES ('402881075f766bbc015f7670f77a0000', '2017-11-01 15:17:16', null, '大撒大撒');
INSERT INTO `rztsysrole` VALUES ('402881075f766bbc015f767398010002', '2017-11-01 15:20:08', '阿迪斯打算333', '发射点犯得上333');

-- ----------------------------
-- Table structure for `rztsysuser`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysuser`;
CREATE TABLE `rztsysuser` (
  `id` varchar(255) NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `deptId` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `loginStatus` int(11) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `realname` varchar(255) DEFAULT NULL,
  `userDelete` int(11) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `userType` int(1) DEFAULT NULL COMMENT '用户类型 0 app用户 1 pc用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysuser
-- ----------------------------
INSERT INTO `rztsysuser` VALUES ('402881075f6c4d1e015f6c4d7f710000', null, '2017-10-30 16:02:19', '402881075f9f7839015f9f834fbd0008', 'shinaiyu2008@126.com', '1', '18653033682', '李四', '0', 'hello', '0');

-- ----------------------------
-- Table structure for `rztsysuserauth`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysuserauth`;
CREATE TABLE `rztsysuserauth` (
  `id` varchar(255) NOT NULL,
  `createtime` datetime DEFAULT NULL,
  `identifier` int(11) DEFAULT NULL,
  `identityType` varchar(255) DEFAULT NULL,
  `lastLoginIp` varchar(255) DEFAULT NULL,
  `lastLoginTime` datetime DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `userId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysuserauth
-- ----------------------------
INSERT INTO `rztsysuserauth` VALUES ('402881075f6c4d1e015f6c4d80350001', '2017-10-30 16:02:19', '0', 'hello', '192.168.1.155', '2017-11-10 10:41:22', '1234567', '402881075f6c4d1e015f6c4d7f710000');
INSERT INTO `rztsysuserauth` VALUES ('402881075f6c4d1e015f6c4d80360002', '2017-10-30 16:02:19', '1', '18653033682', null, null, '1234567', '402881075f6c4d1e015f6c4d7f710000');
INSERT INTO `rztsysuserauth` VALUES ('402881075f6c4d1e015f6c4d80360003', '2017-10-30 16:02:19', '2', 'shinaiyu2008@126.com', null, null, '1234567', '402881075f6c4d1e015f6c4d7f710000');

-- ----------------------------
-- Table structure for `rztsysusergroup`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysusergroup`;
CREATE TABLE `rztsysusergroup` (
  `id` varchar(255) NOT NULL,
  `groupId` varchar(255) DEFAULT NULL,
  `userId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysusergroup
-- ----------------------------

-- ----------------------------
-- Table structure for `rztsysuserrole`
-- ----------------------------
DROP TABLE IF EXISTS `rztsysuserrole`;
CREATE TABLE `rztsysuserrole` (
  `id` varchar(255) NOT NULL,
  `roleId` varchar(255) DEFAULT NULL,
  `userId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rztsysuserrole
-- ----------------------------
INSERT INTO `rztsysuserrole` VALUES ('402881075f80fdcd015f810d53d10000', '402881075f764f94015f765b46e10001', '402881075f6c4d1e015f6c4d7f710000');
INSERT INTO `rztsysuserrole` VALUES ('402881075f80fdcd015f810e8d210001', '402881075f766bbc015f7670f77a0000', '402881075f6c4d1e015f6c4d7f710000');
INSERT INTO `rztsysuserrole` VALUES ('83202ea1c06f11e7938668f728344c35', '402881075f764f94015f765ec8480003', '402881075f6c4d1e015f6c4d7f710000');
