-- MySQL dump 10.13  Distrib 5.7.26, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: hospital
-- ------------------------------------------------------
-- Server version	5.7.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointment` (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `patientid` int(11) DEFAULT NULL COMMENT '患者id',
                             `doctorid` int(11) DEFAULT NULL COMMENT '医生id',
                             `time` date DEFAULT NULL COMMENT '预约时间',
                             `expenses` decimal(10,2) DEFAULT NULL COMMENT '门诊费',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment`
--

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
INSERT INTO `appointment` (`id`, `patientid`, `doctorid`, `time`, `expenses`) VALUES (1,1,2,'2017-03-07',15.00),(7,2,2,'2018-11-27',15.00),(8,3,2,'2018-12-01',15.00),(9,4,1,'2023-02-01',15.00),(10,4,2,'2019-03-05',15.00),(13,3,1,'2019-03-31',15.00),(14,4,1,'2017-02-05',15.00),(15,1,1,'2019-04-06',15.00),(19,1,1,'2019-04-21',1312312.00),(20,1,1,'2019-04-21',21212121.00);
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doctor` (
                        `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `name` varchar(255) DEFAULT NULL COMMENT '姓名',
                        `age` int(11) DEFAULT NULL COMMENT '年龄',
                        `certId` varchar(255) DEFAULT NULL COMMENT '身份证',
                        `sex` int(10) DEFAULT NULL COMMENT '性别',
                        `department` varchar(255) DEFAULT NULL COMMENT '部门',
                        `address` varchar(255) DEFAULT NULL COMMENT '家庭住址',
                        `loginid` int(11) DEFAULT NULL COMMENT '登录',
                        `text` text COMMENT '介绍',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor`
--

LOCK TABLES `doctor` WRITE;
/*!40000 ALTER TABLE `doctor` DISABLE KEYS */;
INSERT INTO `doctor` (`id`, `name`, `age`, `certId`, `sex`, `department`, `address`, `loginid`, `text`) VALUES (1,'张三',32,'666',1,'精神科','江苏/无锡',3,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2'),(2,'李四',32,'777',1,'妇产科','江苏/无锡',4,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2'),(3,'王五',56,'888',1,'妇产科','sasasas',12,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2'),(4,'2323',12,'１１２１２１２１',0,'口腔科','21212',NULL,NULL);
/*!40000 ALTER TABLE `doctor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drugs`
--

DROP TABLE IF EXISTS `drugs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drugs` (
                       `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
                       `name` varchar(255) DEFAULT NULL COMMENT '药品名称',
                       `type` int(10) DEFAULT NULL COMMENT '药品类型',
                       `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
                       `number` int(11) DEFAULT NULL COMMENT '数量',
                       `text` text COMMENT '介绍',
                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drugs`
--

LOCK TABLES `drugs` WRITE;
/*!40000 ALTER TABLE `drugs` DISABLE KEYS */;
INSERT INTO `drugs` (`id`, `name`, `type`, `price`, `number`, `text`) VALUES (1,'111111',4,4.66,100,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2'),(2,'22',1,4.55,100,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2'),(3,'33',2,4.56,100,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2'),(4,'44',3,4.76,100,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2'),(5,'55',4,5.23,100,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2'),(7,'66',5,14.12,100,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2'),(13,'werwer',2,4.66,100,'手动阀手动阀手动阀手动阀啊JKDASJDKLADLASLDKASL1312KASDKLASDKASJFLASFJKSGFJSDAKJFSAKFJSKFJSDKFJSDKLFJSDKLFJSCMxlSKkdosadkfsd看风使舵李开复十六分就开了反射定律攻击速度老师的课反对法工地上可22插卡式西欧的靠谱的看312课程搜房参考2');
/*!40000 ALTER TABLE `drugs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hospitalization`
--

DROP TABLE IF EXISTS `hospitalization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hospitalization` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `floor` varchar(10) DEFAULT NULL COMMENT '楼层',
                                 `bed` varchar(255) DEFAULT NULL COMMENT '床号',
                                 `door` varchar(255) DEFAULT NULL COMMENT '房间号',
                                 `medicalname` varchar(255) DEFAULT NULL COMMENT '病名称',
                                 `patientid` int(11) DEFAULT NULL COMMENT '患者id',
                                 `intime` date DEFAULT NULL COMMENT '住院时间',
                                 `outtime` date DEFAULT NULL COMMENT '出院时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hospitalization`
--

LOCK TABLES `hospitalization` WRITE;
/*!40000 ALTER TABLE `hospitalization` DISABLE KEYS */;
INSERT INTO `hospitalization` (`id`, `floor`, `bed`, `door`, `medicalname`, `patientid`, `intime`, `outtime`) VALUES (1,'3','4','304','qweq',1,'2019-03-07','2019-03-08'),(2,'2','3','305','asd',2,'2019-03-07','2019-03-10'),(3,'2','2','305','asd',3,'2019-03-07','2019-03-10'),(4,'2','1','305','dsad',4,'2019-03-07','2019-03-18'),(5,'3','2','306','add',4,'2019-03-07','2019-03-19'),(8,'1','2','102','adasdasd',3,'2019-03-28','2019-03-31'),(9,'1','1','102','脑残',1,'2019-04-06',NULL);
/*!40000 ALTER TABLE `hospitalization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `illness`
--

DROP TABLE IF EXISTS `illness`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `illness` (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `name` varchar(255) DEFAULT NULL,
                         `text` varchar(255) DEFAULT NULL,
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `illness`
--

LOCK TABLES `illness` WRITE;
/*!40000 ALTER TABLE `illness` DISABLE KEYS */;
INSERT INTO `illness` (`id`, `name`, `text`) VALUES (1,'狂犬病','xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'),(2,'艾滋病','xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'),(3,'甲型H1N1','xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'),(4,'dasd','xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'),(5,'dasd','xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'),(6,'wqeqwe','xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');
/*!40000 ALTER TABLE `illness` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `login` (
                       `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                       `username` varchar(255) DEFAULT NULL COMMENT '账号',
                       `password` varchar(255) DEFAULT NULL COMMENT '密码',
                       `role` int(255) DEFAULT NULL COMMENT '权限',
                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login`
--

LOCK TABLES `login` WRITE;
/*!40000 ALTER TABLE `login` DISABLE KEYS */;
INSERT INTO `login` (`id`, `username`, `password`, `role`) VALUES (1,'admin1','123456',1),(2,'admin2','123456',1),(3,'zhangsan','1234567',2),(4,'lisi','123456',2),(5,'zhaoone','123456',3),(6,'zhaotwo','123456',3),(7,'zhaothree','123456',3),(8,'zhaofour','123456',3),(9,'zhaofive','123456',3),(10,'admin3','123456',1),(11,'admin4','123456',1),(12,'wangwu','123456',2),(17,'admin1qwqwqw','123456',1);
/*!40000 ALTER TABLE `login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicalhistory`
--

DROP TABLE IF EXISTS `medicalhistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medicalhistory` (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `patientid` int(10) DEFAULT NULL COMMENT '患者id',
                                `name` varchar(255) DEFAULT NULL COMMENT '病史名称',
                                `time` date DEFAULT NULL COMMENT '患病时间',
                                `hospitalizationid` int(11) DEFAULT NULL COMMENT '住院信息',
                                `doctorid` int(11) DEFAULT NULL COMMENT '确诊人',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicalhistory`
--

LOCK TABLES `medicalhistory` WRITE;
/*!40000 ALTER TABLE `medicalhistory` DISABLE KEYS */;
INSERT INTO `medicalhistory` (`id`, `patientid`, `name`, `time`, `hospitalizationid`, `doctorid`) VALUES (1,1,'狂犬病','2019-03-07',1,1),(2,2,'狂犬病','2019-03-07',2,1),(3,3,'狂犬病','2019-03-07',3,2),(4,4,'狂犬病','2019-03-07',4,2),(5,1,'狂犬病','2019-03-23',1,4);
/*!40000 ALTER TABLE `medicalhistory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `option`
--

DROP TABLE IF EXISTS `option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `option` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `name` varchar(255) DEFAULT NULL,
                        `type` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `option`
--

LOCK TABLES `option` WRITE;
/*!40000 ALTER TABLE `option` DISABLE KEYS */;
INSERT INTO `option` (`id`, `name`, `type`) VALUES (1,'大血','血液科室'),(2,'小血','血液科室'),(3,'B超（彩超）','超生室'),(4,'心电图','心电图室'),(5,'大便','厕所门口自行拿管'),(6,'小便','厕所门口自行拿管'),(7,'CT','放射科室'),(8,'X光片','放射科室'),(9,'核磁共振','放射科室');
/*!40000 ALTER TABLE `option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient` (
                         `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
                         `name` varchar(255) DEFAULT NULL COMMENT '姓名',
                         `age` int(10) DEFAULT NULL COMMENT '年龄',
                         `certId` varchar(255) DEFAULT NULL COMMENT '身份证',
                         `sex` int(10) DEFAULT NULL COMMENT '性别',
                         `address` varchar(255) DEFAULT NULL COMMENT '家庭住址',
                         `Hospitalizationid` int(10) DEFAULT NULL COMMENT '住院信息',
                         `drugsids` varchar(255) DEFAULT NULL COMMENT '药品信息',
                         `isout` int(255) DEFAULT NULL COMMENT '是否出院',
                         `appointmentid` int(11) DEFAULT NULL COMMENT '预约信息',
                         `loginid` int(11) DEFAULT NULL COMMENT '登录',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` (`id`, `name`, `age`, `certId`, `sex`, `address`, `Hospitalizationid`, `drugsids`, `isout`, `appointmentid`, `loginid`) VALUES (1,'赵老一',20,'111',0,'江苏/苏州',1,'1,2,3,4',0,23,5),(2,'赵老二',21,'222',0,'江苏/无锡',2,'2',0,7,6),(3,'赵老三',22,'333',0,'江苏/淮安',3,'3',1,8,7),(4,'赵老四',23,'444',0,'江苏/镇江',4,'4',2,9,8),(5,'赵老五',32,'555',1,'江苏/徐州',1,'2',1,9,9),(9,'sqeqe１１１１',121,'2121',1,'飒飒撒所',NULL,NULL,0,NULL,NULL),(10,'张伟',12,'1111',0,'212121212121',NULL,NULL,0,NULL,NULL);
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-02  2:04:24