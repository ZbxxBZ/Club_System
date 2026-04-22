-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: club_system
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL,
  `biz_type` varchar(30) NOT NULL,
  `biz_id` bigint NOT NULL,
  `op_type` varchar(30) NOT NULL COMMENT 'CREATE/UPDATE/APPROVE/REJECT/PAY',
  `op_user_id` bigint DEFAULT NULL,
  `op_user_role` varchar(30) DEFAULT NULL,
  `op_at` datetime(3) NOT NULL,
  `request_id` varchar(64) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `before_json` json DEFAULT NULL,
  `after_json` json DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_audit_biz` (`biz_type`,`biz_id`),
  KEY `idx_audit_user_time` (`op_user_id`,`op_at`),
  KEY `idx_audit_time` (`op_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (283908217180160,'CLUB_APPLY',283908098691073,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 13:45:29.586',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','1','2','学校管理员推进审批流程'),(283908300304384,'CLUB_APPLY',283908098691073,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 13:45:49.879',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','2','3','学校管理员推进审批流程'),(283908305346560,'CLUB_APPLY',283908098691073,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 13:45:51.110',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','3','4','学校管理员推进审批流程'),(283908794044416,'CLUB_CANCEL',283908765704192,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 13:47:50.421',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','1','2','学校管理员推进社团注销审核流程'),(283908799762432,'CLUB_CANCEL',283908765704192,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 13:47:51.817',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','2','3','学校管理员推进社团注销审核流程'),(283908804567040,'CLUB_CANCEL',283908765704192,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 13:47:52.990',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','3','4','学校管理员推进社团注销审核流程'),(283908845010944,'CLUB_MANAGE',283908098691072,'DELETE',2001,'SCHOOL_ADMIN','2026-03-13 13:48:02.865',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','{\"clubId\": 283908098691072, \"status\": 5, \"purpose\": \"test社团宗旨更改\", \"category\": \"文化类\", \"clubName\": \"test社团名称\", \"applyStatus\": 4, \"instructorName\": \"test指导教师\"}','{\"deleted\": true}','delete club by school admin'),(283912535764992,'CLUB_APPLY',283912415539201,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 14:03:03.927',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','1','2','学校管理员推进审批流程'),(283912542580736,'CLUB_APPLY',283912415539201,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 14:03:05.591',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','2','3','学校管理员推进审批流程'),(283912600518656,'CLUB_APPLY',283912415539201,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 14:03:19.736',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','3','4','学校管理员推进审批流程'),(283912633319424,'CLUB_STATUS',283912415539200,'FREEZE',2001,'SCHOOL_ADMIN','2026-03-13 14:03:27.745',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','2','3','学校管理员手动冻结社团'),(283912639873024,'CLUB_STATUS',283912415539200,'UNFREEZE',2001,'SCHOOL_ADMIN','2026-03-13 14:03:29.344',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','3','2','学校管理员手动解冻社团'),(283912865652736,'CLUB_CANCEL',283912786989056,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 14:04:24.466',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','1','2','学校管理员推进社团注销审核流程'),(283912868937728,'CLUB_CANCEL',283912786989056,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 14:04:25.268',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','2','3','学校管理员推进社团注销审核流程'),(283912870899712,'CLUB_CANCEL',283912786989056,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-13 14:04:25.748',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','3','4','学校管理员推进社团注销审核流程'),(283912907444224,'CLUB_MANAGE',283912415539200,'DELETE',2001,'SCHOOL_ADMIN','2026-03-13 14:04:34.669',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','{\"clubId\": 283912415539200, \"status\": 5, \"purpose\": \"test\", \"category\": \"文化类\", \"clubName\": \"test\", \"applyStatus\": 4, \"instructorName\": \"test\"}','{\"deleted\": true}','delete club by school admin'),(285095483219968,'CLUB_APPLY',285095420702721,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-16 22:16:29.458',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','1','2','学校管理员推进审批流程'),(285095487275008,'CLUB_APPLY',285095420702721,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-16 22:16:30.448',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','2','3','学校管理员推进审批流程'),(285095489363968,'CLUB_APPLY',285095420702721,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-16 22:16:30.958',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0','3','4','学校管理员推进审批流程'),(285129927241728,'CLUB_APPLY',285129888755713,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-17 00:36:38.643',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','学校管理员推进审批流程'),(285129931169792,'CLUB_APPLY',285129888755713,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-17 00:36:39.603',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3','学校管理员推进审批流程'),(285129933156352,'CLUB_APPLY',285129888755713,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-17 00:36:40.087',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','3','4','学校管理员推进审批流程'),(285156278362112,'CLUB_APPLY',285156207779841,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-17 02:23:52.023',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','学校管理员推进审批流程'),(285156282695680,'CLUB_APPLY',285156207779841,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-17 02:23:53.080',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3','学校管理员推进审批流程'),(285156287406080,'CLUB_APPLY',285156207779841,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-17 02:23:54.231',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','3','4','学校管理员推进审批流程'),(285158014468096,'CLUB_JOIN_APPLY',285157983768576,'APPROVE',2003,'CLUB_ADMIN','2026-03-17 02:30:55.868',NULL,NULL,NULL,NULL,NULL,'join apply approved'),(285158575222784,'CLUB_JOIN_APPLY',285158497918976,'REJECT',2003,'CLUB_ADMIN','2026-03-17 02:33:12.778',NULL,NULL,NULL,NULL,NULL,'抱歉无法招收'),(285160228380672,'CLUB_JOIN_APPLY',285158718541824,'APPROVE',2003,'CLUB_ADMIN','2026-03-17 02:39:56.378',NULL,NULL,NULL,NULL,NULL,'join apply approved'),(285494096179200,'CLUB_STATUS',285156207779840,'FREEZE',2001,'SCHOOL_ADMIN','2026-03-18 01:18:27.076',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3','学校管理员手动冻结社团'),(285494155239424,'CLUB_STATUS',285156207779840,'UNFREEZE',2001,'SCHOOL_ADMIN','2026-03-18 01:18:41.495',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','3','2','学校管理员手动解冻社团'),(285496983470080,'CLUB_CANCEL',285496960630784,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-18 01:30:11.981',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','学校管理员推进社团注销审核流程'),(285496986640384,'CLUB_CANCEL',285496960630784,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-18 01:30:12.755',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3','学校管理员推进社团注销审核流程'),(285496989564928,'CLUB_CANCEL',285496960630784,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-18 01:30:13.469',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','3','4','学校管理员推进社团注销审核流程'),(285498462093312,'CLUB_APPLY',285498425569281,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-18 01:36:12.972',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','学校管理员推进审批流程'),(285498465320960,'CLUB_APPLY',285498425569281,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-18 01:36:13.761',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3','学校管理员推进审批流程'),(285498468134912,'CLUB_APPLY',285498425569281,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-18 01:36:14.448',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','3','4','学校管理员推进审批流程'),(285499133751296,'CLUB_JOIN_APPLY',285499087720448,'APPROVE',2003,'CLUB_ADMIN','2026-03-18 01:38:56.947',NULL,NULL,NULL,NULL,NULL,'join apply approved'),(285499799973888,'CLUB_JOIN_APPLY',285499684401152,'APPROVE',2003,'CLUB_ADMIN','2026-03-18 01:41:39.601',NULL,NULL,NULL,NULL,NULL,'join apply approved'),(286094662656000,'CLUB_CANCEL',286094602571776,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-19 18:02:09.751',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','学校管理员推进社团注销审核流程'),(286094666895360,'CLUB_CANCEL',286094602571776,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-19 18:02:10.785',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3','学校管理员推进社团注销审核流程'),(286094669287424,'CLUB_CANCEL',286094602571776,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-19 18:02:11.369',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','3','4','学校管理员推进社团注销审核流程'),(286095562145792,'CLUB_APPLY',286095523741697,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-19 18:05:49.352',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','学校管理员推进审批流程'),(286095564402688,'CLUB_APPLY',286095523741697,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-19 18:05:49.904',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3','学校管理员推进审批流程'),(286095566741504,'CLUB_APPLY',286095523741697,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-19 18:05:50.474',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','3','4','学校管理员推进审批流程'),(286095820595201,'CLUB_JOIN_APPLY',286095746416640,'APPROVE',2003,'CLUB_ADMIN','2026-03-19 18:06:52.449',NULL,NULL,NULL,NULL,NULL,'join apply approved'),(286098507595776,'CLUB_JOIN_APPLY',286098448117760,'REJECT',2003,'CLUB_ADMIN','2026-03-19 18:17:48.456',NULL,NULL,NULL,NULL,NULL,'test'),(286387555225600,'CLUB_APPLY',286387520438273,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-20 13:53:56.726',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','学校管理员推进审批流程'),(286387558191104,'CLUB_APPLY',286387520438273,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-20 13:53:57.450',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3','学校管理员推进审批流程'),(286387562688512,'CLUB_APPLY',286387520438273,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-20 13:53:58.547',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','3','4','学校管理员推进审批流程'),(286394859364352,'CLUB_JOIN_APPLY',286394820079616,'APPROVE',2003,'CLUB_ADMIN','2026-03-20 14:23:39.959',NULL,NULL,NULL,NULL,NULL,'join apply approved'),(286394971717632,'CLUB_JOIN_APPLY',286394933559296,'REJECT',2003,'CLUB_ADMIN','2026-03-20 14:24:07.392',NULL,NULL,NULL,NULL,NULL,'123'),(286799282749440,'CLUB_JOIN_APPLY',286394933559296,'REJECT',2003,'CLUB_ADMIN','2026-03-21 17:49:16.140',NULL,NULL,NULL,NULL,NULL,'test3再次驳回'),(286799293415424,'CLUB_JOIN_APPLY',286795914035200,'APPROVE',2003,'CLUB_ADMIN','2026-03-21 17:49:18.741',NULL,NULL,NULL,NULL,NULL,'join apply approved'),(286809713528832,'EVENT',286808357945344,'REJECT',2001,'SCHOOL_ADMIN','2026-03-21 18:31:42.717',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5',''),(286809855283200,'EVENT',286808357945344,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-21 18:32:17.325',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(286811618091008,'EVENT',286811517988864,'REJECT',2001,'SCHOOL_ADMIN','2026-03-21 18:39:27.698',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test'),(286813715873792,'EVENT',286811517988864,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-21 18:47:59.853',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(286816867745792,'EVENT',286816315502592,'REJECT',2001,'SCHOOL_ADMIN','2026-03-21 19:00:49.354',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test'),(286817028280320,'EVENT',286816315502592,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-21 19:01:28.546',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(286838857265152,'EVENT',286837928271872,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-21 20:30:17.887',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(287565454761984,'EVENT',287565371166720,'REJECT',2001,'SCHOOL_ADMIN','2026-03-23 21:46:49.854',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test1驳回'),(287565560385536,'EVENT',287565371166720,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-23 21:47:15.642',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288680596987904,'CLUB_JOIN_APPLY',288680539918336,'APPROVE',2003,'CLUB_ADMIN','2026-03-27 01:24:21.371',NULL,NULL,NULL,NULL,NULL,'join apply approved'),(288682874159104,'EVENT',288682819551232,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 01:33:37.325',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288700651388928,'EVENT',288700572626944,'REJECT',2001,'SCHOOL_ADMIN','2026-03-27 02:45:57.469',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test3驳回'),(288702999236608,'EVENT',288702910406656,'REJECT',2001,'SCHOOL_ADMIN','2026-03-27 02:55:30.674',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test测试驳回'),(288703181459456,'EVENT',288702910406656,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 02:56:15.162',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288709788839936,'EVENT',288709742059520,'REJECT',2001,'SCHOOL_ADMIN','2026-03-27 03:23:08.292',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test'),(288709884166144,'EVENT',288709742059520,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 03:23:31.564',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288714264043520,'EVENT',288714192293888,'REJECT',2001,'SCHOOL_ADMIN','2026-03-27 03:41:20.870',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5',''),(288714384703488,'EVENT',288714192293888,'REJECT',2001,'SCHOOL_ADMIN','2026-03-27 03:41:50.329',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test驳回'),(288714446049280,'EVENT',288714192293888,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 03:42:05.305',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288717147848704,'EVENT',288717079490560,'REJECT',2001,'SCHOOL_ADMIN','2026-03-27 03:53:04.925',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test'),(288717238210560,'EVENT',288717079490560,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 03:53:26.985',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288720291962880,'EVENT',288720264749056,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 04:05:52.531',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288745832108032,'EVENT',288745777213440,'REJECT',2001,'SCHOOL_ADMIN','2026-03-27 05:49:47.917',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test'),(288745951440896,'EVENT',288745777213440,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 05:50:17.052',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288748253634560,'EVENT',288748204724224,'REJECT',2001,'SCHOOL_ADMIN','2026-03-27 05:59:39.110',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','test'),(288748305760256,'EVENT',288748204724224,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 05:59:51.836',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288749329072128,'EVENT',288749296308224,'REJECT',2001,'SCHOOL_ADMIN','2026-03-27 06:04:01.669',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','5','1234'),(288749387223040,'EVENT',288749296308224,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 06:04:15.866',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(288870426083328,'EVENT',288870377975808,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-27 14:16:46.369',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(289365334224896,'INCOME',289365334196224,'CREATE',2003,'CLUB_ADMIN','2026-03-28 23:50:33.544',NULL,NULL,NULL,NULL,NULL,'income created'),(289367800725504,'EXPENSE',289367800696832,'CREATE',2003,'CLUB_ADMIN','2026-03-29 00:00:35.718',NULL,NULL,NULL,NULL,NULL,'expense auto-approved (<=500)'),(289367948828672,'EXPENSE',289367948824576,'CREATE',2003,'CLUB_ADMIN','2026-03-29 00:01:11.882',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(289368750583808,'EXPENSE',289368750571520,'CREATE',2003,'CLUB_ADMIN','2026-03-29 00:04:27.620',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(289377784193024,'EXPENSE',289377784160256,'CREATE',2003,'CLUB_ADMIN','2026-03-29 00:41:13.087',NULL,NULL,NULL,NULL,NULL,'expense auto-approved (<=500)'),(289381207126016,'EXPENSE',289368750571520,'REJECT',2001,'SCHOOL_ADMIN','2026-03-29 00:55:08.772',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','3','test'),(289381330038784,'EXPENSE',289368750571520,'RESUBMIT',2003,'CLUB_ADMIN','2026-03-29 00:55:38.777',NULL,NULL,NULL,NULL,NULL,'expense resubmitted, pending school review (>500)'),(289383508221952,'INCOME',289383508205568,'CREATE',2003,'CLUB_ADMIN','2026-03-29 01:04:30.558',NULL,NULL,NULL,NULL,NULL,'income created'),(289383657537536,'EXPENSE',289383657521152,'CREATE',2003,'CLUB_ADMIN','2026-03-29 01:05:07.013',NULL,NULL,NULL,NULL,NULL,'expense auto-approved (<=500)'),(289383989735424,'EXPENSE',289383989727232,'CREATE',2003,'CLUB_ADMIN','2026-03-29 01:06:28.118',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(289384875048960,'EXPENSE',289383989727232,'REJECT',2001,'SCHOOL_ADMIN','2026-03-29 01:10:04.260',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','3','缺少发票'),(289384943820800,'EXPENSE',289383989727232,'RESUBMIT',2003,'CLUB_ADMIN','2026-03-29 01:10:21.050',NULL,NULL,NULL,NULL,NULL,'expense resubmitted, pending school review (>500)'),(289385004773376,'EXPENSE',289383989727232,'APPROVE',2001,'SCHOOL_ADMIN','2026-03-29 01:10:35.932',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','school admin approved expense'),(289385659064320,'INCOME',289385659052032,'CREATE',2003,'CLUB_ADMIN','2026-03-29 01:13:15.668',NULL,NULL,NULL,NULL,NULL,'income created'),(289386067181568,'EXPENSE',289386067173376,'CREATE',2003,'CLUB_ADMIN','2026-03-29 01:14:55.307',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(292560568897536,'INCOME',292560568811520,'CREATE',2003,'CLUB_ADMIN','2026-04-07 00:32:00.120',NULL,NULL,NULL,NULL,NULL,'income created'),(292560820764673,'EXPENSE',292560820748288,'CREATE',2003,'CLUB_ADMIN','2026-04-07 00:33:01.629',NULL,NULL,NULL,NULL,NULL,'expense auto-approved (<=500)'),(292560945250305,'EXPENSE',292560945250304,'CREATE',2003,'CLUB_ADMIN','2026-04-07 00:33:32.025',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(292561256669184,'EXPENSE',292560945250304,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-07 00:34:48.055',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','school admin approved expense'),(292561424064512,'INCOME',292561424031744,'CREATE',2003,'CLUB_ADMIN','2026-04-07 00:35:28.915',NULL,NULL,NULL,NULL,NULL,'income created'),(292561646309376,'EXPENSE',292561646305280,'CREATE',2003,'CLUB_ADMIN','2026-04-07 00:36:23.180',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(292561710436352,'EXPENSE',292561646305280,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-07 00:36:38.838',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','school admin approved expense'),(292755164774401,'INCOME',292755164758016,'CREATE',2003,'CLUB_ADMIN','2026-04-07 13:43:48.897',NULL,NULL,NULL,NULL,NULL,'income created'),(292755290992641,'EXPENSE',292755290963968,'CREATE',2003,'CLUB_ADMIN','2026-04-07 13:44:19.709',NULL,NULL,NULL,NULL,NULL,'expense auto-approved (<=500)'),(292755423010817,'EXPENSE',292755423010816,'CREATE',2003,'CLUB_ADMIN','2026-04-07 13:44:51.947',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(292755512950784,'EXPENSE',292755423010816,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-07 13:45:13.904',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','school admin approved expense'),(292755748990978,'INCOME',292755748990976,'CREATE',2003,'CLUB_ADMIN','2026-04-07 13:46:11.532',NULL,NULL,NULL,NULL,NULL,'income created'),(292755852324866,'EXPENSE',292755852324864,'CREATE',2003,'CLUB_ADMIN','2026-04-07 13:46:36.759',NULL,NULL,NULL,NULL,NULL,'expense auto-approved (<=500)'),(292756024832001,'EXPENSE',292756024832000,'CREATE',2003,'CLUB_ADMIN','2026-04-07 13:47:18.875',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(292756074958848,'EXPENSE',292756024832000,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-07 13:47:31.113',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','school admin approved expense'),(292759551746048,'INCOME',292759551733760,'CREATE',2003,'CLUB_ADMIN','2026-04-07 14:01:39.935',NULL,NULL,NULL,NULL,NULL,'income created'),(292759669981184,'EXPENSE',292759669972992,'CREATE',2003,'CLUB_ADMIN','2026-04-07 14:02:08.803',NULL,NULL,NULL,NULL,NULL,'expense auto-approved (<=500)'),(292759744417793,'EXPENSE',292759744417792,'CREATE',2003,'CLUB_ADMIN','2026-04-07 14:02:26.977',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(292759790915584,'EXPENSE',292759744417792,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-07 14:02:38.329',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','school admin approved expense'),(292777798238208,'EVENT',292777651609600,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-07 15:15:54.648',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(295239938277376,'CLUB_APPLY',295239663321089,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-14 14:14:23.057',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','学校管理员推进审批流程'),(295239951929344,'CLUB_APPLY',295239663321089,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-14 14:14:26.389',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3','学校管理员推进审批流程'),(295239957237760,'CLUB_APPLY',295239663321089,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-14 14:14:27.685',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','3','4','学校管理员推进审批流程'),(295240838369280,'CLUB_JOIN_APPLY',295240677662720,'APPROVE',295239277260800,'CLUB_ADMIN','2026-04-14 14:18:02.794',NULL,NULL,NULL,NULL,NULL,'join apply approved'),(295241832873984,'EVENT',295241742966784,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-14 14:22:05.605',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','2','3',NULL),(295242544070656,'INCOME',295242544041984,'CREATE',295239277260800,'CLUB_ADMIN','2026-04-14 14:24:59.229',NULL,NULL,NULL,NULL,NULL,'income created'),(295242849304576,'EXPENSE',295242849251328,'CREATE',295239277260800,'CLUB_ADMIN','2026-04-14 14:26:13.744',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(295242900750336,'EXPENSE',295242849251328,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-14 14:26:26.317',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0','1','2','school admin approved expense'),(295410978705408,'CLUB_REVIEW',295410786238464,'REJECT',2001,'SCHOOL_ADMIN','2026-04-15 01:50:20.973',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36 Edg/147.0.0.0','2','4','不合格'),(295411158425600,'CLUB_REVIEW',295410786238464,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-15 01:51:04.851',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36 Edg/147.0.0.0','2','3',NULL),(295417420161024,'CLUB_REVIEW',295417316147200,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-15 02:16:33.594',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36 Edg/147.0.0.0','2','3',NULL),(295418302279680,'CLUB_REVIEW',295417550245888,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-15 02:20:08.955',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36 Edg/147.0.0.0','2','3',NULL),(295422059220992,'EXPENSE',890003,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-15 02:35:26.178',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36 Edg/147.0.0.0','1','2','school admin approved expense'),(295422909390848,'EXPENSE',295422909378560,'CREATE',900009,'CLUB_ADMIN','2026-04-15 02:38:53.735',NULL,NULL,NULL,NULL,NULL,'expense pending school review (>500)'),(296025303433216,'EVENT',296025245392896,'APPROVE',2001,'SCHOOL_ADMIN','2026-04-16 19:30:02.597',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36 Edg/147.0.0.0','2','3',NULL);
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `budget`
--

DROP TABLE IF EXISTS `budget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `budget` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `budget_year` int NOT NULL,
  `budget_total` decimal(14,2) NOT NULL DEFAULT '0.00',
  `used_total` decimal(14,2) NOT NULL DEFAULT '0.00',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1草稿 2生效 3调整中',
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_budget_club_year` (`club_id`,`budget_year`),
  CONSTRAINT `fk_budget_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `budget`
--

LOCK TABLES `budget` WRITE;
/*!40000 ALTER TABLE `budget` DISABLE KEYS */;
/*!40000 ALTER TABLE `budget` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club`
--

DROP TABLE IF EXISTS `club`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club` (
  `id` bigint NOT NULL,
  `club_code` varchar(32) NOT NULL,
  `club_name` varchar(100) NOT NULL,
  `category` varchar(30) NOT NULL,
  `purpose` varchar(500) DEFAULT NULL,
  `description` text,
  `instructor_name` varchar(50) DEFAULT NULL,
  `established_date` date DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1筹建 2正常 3限期整改/冻结 4待注销 5已注销',
  `recruit_start_at` datetime DEFAULT NULL,
  `recruit_end_at` datetime DEFAULT NULL,
  `recruit_limit` int DEFAULT NULL,
  `recruit_status` varchar(10) NOT NULL DEFAULT 'CLOSED' COMMENT 'OPEN/CLOSED',
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_club_code` (`club_code`),
  UNIQUE KEY `uk_club_name` (`club_name`),
  KEY `idx_status_category` (`status`,`category`),
  KEY `idx_recruit_status_time` (`recruit_status`,`recruit_start_at`,`recruit_end_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club`
--

LOCK TABLES `club` WRITE;
/*!40000 ALTER TABLE `club` DISABLE KEYS */;
INSERT INTO `club` VALUES (800001,'CLUB800001','篮球社','体育类','强身健体，以球会友','校篮球社成立于2020年，定期组织训练和比赛，多次获得校级联赛冠军。','王教练','2020-09-01',2,'2026-03-15 00:00:00','2026-06-30 23:59:59',50,'OPEN','2026-03-01 10:00:00.000','2026-03-15 10:00:00.000',900009,900009,0),(800002,'CLUB800002','编程社','科技类','探索技术，编码未来','编程社致力于培养同学们的编程能力，定期举办算法竞赛和技术分享。','李老师','2021-03-01',2,'2026-03-15 00:00:00','2026-06-30 23:59:59',80,'OPEN','2026-03-01 10:00:00.000','2026-03-15 10:00:00.000',900010,900010,0),(800003,'CLUB800003','吉他社','文艺类','弦歌不辍，音乐相伴','吉他社每周组织弹唱练习，学期末举办校园民谣演唱会。','赵老师','2019-09-01',2,NULL,NULL,NULL,'CLOSED','2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',900009,900009,0),(800004,'CLUB800004','摄影协会','文化类','记录生活，定格美好','摄影协会拥有专业器材，定期组织外拍活动和摄影展览。','钱老师','2020-03-01',2,'2026-04-01 00:00:00','2026-05-31 23:59:59',30,'OPEN','2026-03-01 10:00:00.000','2026-04-01 10:00:00.000',900009,900009,0),(800005,'CLUB800005','志愿者协会','公益类','奉献爱心，服务社会','志愿者协会组织各类公益活动，累计服务时长超过10000小时。','孙老师','2018-09-01',3,NULL,NULL,NULL,'CLOSED','2026-03-01 10:00:00.000','2026-04-05 10:00:00.000',900009,900009,0),(286387520438272,'CLUB286387520438272','test','文化类','test社团宗旨','test社团简介','指导教师','2026-03-20',2,'2026-03-20 14:23:00','2026-04-23 14:23:00',10,'OPEN','2026-03-20 13:53:48.232','2026-03-27 01:23:15.041',2003,2003,0),(295239663321088,'CLUB295239663321088','test99','文化类','test999','test社团简介','张老师','2026-04-14',2,'2026-04-14 14:16:00','2026-04-15 14:16:00',100,'OPEN','2026-04-14 14:13:15.928','2026-04-15 01:51:04.842',295239277260800,2001,0);
/*!40000 ALTER TABLE `club` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club_apply`
--

DROP TABLE IF EXISTS `club_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club_apply` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `initiator_user_id` bigint NOT NULL,
  `charter_url` varchar(255) DEFAULT NULL,
  `instructor_name` varchar(50) DEFAULT NULL COMMENT '指导教师姓名',
  `instructor_proof_url` varchar(255) DEFAULT NULL COMMENT '指导教师证明文件对象存储地址',
  `apply_status` tinyint NOT NULL COMMENT '1待初审 2答辩中 3公示中 4通过 5驳回',
  `current_step` varchar(30) DEFAULT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_club_status` (`club_id`,`apply_status`),
  CONSTRAINT `fk_club_apply_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club_apply`
--

LOCK TABLES `club_apply` WRITE;
/*!40000 ALTER TABLE `club_apply` DISABLE KEYS */;
INSERT INTO `club_apply` VALUES (810001,800001,900009,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/686a96b08ad74c3e84d6e4cc41aa7445.docx','王教练','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1333226afb354e52ae94e7e2d71531c9.docx',4,'APPROVED','审批通过','2026-03-01 10:00:00.000','2026-03-05 10:00:00.000',900009,2001,0),(810002,800002,900010,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/686a96b08ad74c3e84d6e4cc41aa7445.docx','李老师','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1333226afb354e52ae94e7e2d71531c9.docx',4,'APPROVED','审批通过','2026-03-01 10:00:00.000','2026-03-05 10:00:00.000',900010,2001,0),(810003,800003,900001,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/686a96b08ad74c3e84d6e4cc41aa7445.docx','赵老师','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1333226afb354e52ae94e7e2d71531c9.docx',4,'APPROVED','审批通过','2026-03-01 10:00:00.000','2026-03-05 10:00:00.000',900001,2001,0),(810004,800004,900002,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/686a96b08ad74c3e84d6e4cc41aa7445.docx','钱老师','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1333226afb354e52ae94e7e2d71531c9.docx',4,'APPROVED','审批通过','2026-03-01 10:00:00.000','2026-03-05 10:00:00.000',900002,2001,0),(810005,800005,900003,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/686a96b08ad74c3e84d6e4cc41aa7445.docx','孙老师','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1333226afb354e52ae94e7e2d71531c9.docx',4,'APPROVED','审批通过','2026-03-01 10:00:00.000','2026-03-05 10:00:00.000',900003,2001,0),(286387520438273,286387520438272,2003,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/686a96b08ad74c3e84d6e4cc41aa7445.docx','指导教师','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1333226afb354e52ae94e7e2d71531c9.docx',4,'APPROVED','学校管理员推进审批流程','2026-03-20 13:53:48.232','2026-03-20 13:53:58.541',2003,2001,0),(295239663321089,295239663321088,295239277260800,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/41e2402fbe5349908ba53cb433935f9f.docx','张老师','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/9d29d9fe7a8f46188fbf5f9ad713fb60.docx',4,'APPROVED','学校管理员推进审批流程','2026-04-14 14:13:15.928','2026-04-14 14:14:27.672',295239277260800,2001,0);
/*!40000 ALTER TABLE `club_apply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club_cancel`
--

DROP TABLE IF EXISTS `club_cancel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club_cancel` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `apply_type` tinyint NOT NULL COMMENT '1主动 2强制',
  `apply_reason` varchar(500) DEFAULT NULL,
  `asset_settlement_url` varchar(255) DEFAULT NULL,
  `cancel_status` tinyint NOT NULL COMMENT '1待公示 2待经费结清 3待资产移交 4已完成 5驳回',
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_club_cancel_status` (`club_id`,`cancel_status`),
  CONSTRAINT `fk_club_cancel_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club_cancel`
--

LOCK TABLES `club_cancel` WRITE;
/*!40000 ALTER TABLE `club_cancel` DISABLE KEYS */;
/*!40000 ALTER TABLE `club_cancel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club_join_apply`
--

DROP TABLE IF EXISTS `club_join_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club_join_apply` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `apply_reason` varchar(500) DEFAULT NULL,
  `self_intro` varchar(500) DEFAULT NULL COMMENT '个人简介',
  `join_status` tinyint NOT NULL DEFAULT '1' COMMENT '1待批准 2已加入 3已驳回',
  `review_user_id` bigint DEFAULT NULL,
  `reviewed_at` datetime DEFAULT NULL,
  `reject_reason` varchar(500) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_join_user_status` (`user_id`,`join_status`),
  KEY `idx_join_club_status` (`club_id`,`join_status`),
  CONSTRAINT `fk_club_join_apply_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`),
  CONSTRAINT `fk_club_join_apply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club_join_apply`
--

LOCK TABLES `club_join_apply` WRITE;
/*!40000 ALTER TABLE `club_join_apply` DISABLE KEYS */;
INSERT INTO `club_join_apply` VALUES (840001,800001,900007,'我热爱篮球，想加入篮球社一起训练','擅长控球后卫',1,NULL,NULL,NULL,'2026-04-10 10:00:00.000','2026-04-10 10:00:00.000',900007,900007,0),(840002,800002,900008,'想学习编程，提升自己的技术能力','有Python基础',1,NULL,NULL,NULL,'2026-04-10 11:00:00.000','2026-04-10 11:00:00.000',900008,900008,0),(840003,800001,900004,'喜欢打篮球','身高185',2,900009,'2026-03-12 10:00:00',NULL,'2026-03-11 10:00:00.000','2026-03-12 10:00:00.000',900004,900009,0),(840004,800003,900007,'想学吉他','零基础',3,900009,'2026-04-08 10:00:00','本学期招新已结束，请下学期再申请','2026-04-07 10:00:00.000','2026-04-08 10:00:00.000',900007,900009,0),(840005,800004,900003,'对摄影非常感兴趣','有单反相机',1,NULL,NULL,NULL,'2026-04-09 10:00:00.000','2026-04-09 10:00:00.000',900003,900003,0),(840101,800001,900001,'热爱篮球运动，高中时是校队成员','擅长前锋位置',2,900009,'2026-03-10 10:00:00',NULL,'2026-03-09 10:00:00.000','2026-03-10 10:00:00.000',900001,900009,0),(840102,800001,900002,'想通过篮球锻炼身体','喜欢投三分',2,900009,'2026-03-10 10:00:00',NULL,'2026-03-09 11:00:00.000','2026-03-10 10:00:00.000',900002,900009,0),(840103,800001,900003,'参加过市级篮球联赛','中锋，身高190',2,900009,'2026-03-10 10:00:00',NULL,'2026-03-09 12:00:00.000','2026-03-10 10:00:00.000',900003,900009,0),(840104,800002,900005,'对算法和数据结构很感兴趣','参加过蓝桥杯',2,900010,'2026-03-10 10:00:00',NULL,'2026-03-09 10:00:00.000','2026-03-10 10:00:00.000',900005,900010,0),(840105,800002,900006,'想学习Java和Web开发','有C语言基础',2,900010,'2026-03-11 10:00:00',NULL,'2026-03-10 10:00:00.000','2026-03-11 10:00:00.000',900006,900010,0),(840106,800002,900007,'对编程充满热情','自学过Python',2,900010,'2026-03-12 10:00:00',NULL,'2026-03-11 10:00:00.000','2026-03-12 10:00:00.000',900007,900010,0),(840107,800003,900001,'从小喜欢音乐，想学吉他','有钢琴基础',2,900009,'2026-03-10 10:00:00',NULL,'2026-03-09 10:00:00.000','2026-03-10 10:00:00.000',900001,900009,0),(840108,800003,900005,'想学民谣弹唱','零基础但很努力',2,900009,'2026-03-11 10:00:00',NULL,'2026-03-10 10:00:00.000','2026-03-11 10:00:00.000',900005,900009,0),(840109,800004,900002,'喜欢拍照记录生活','有微单相机',2,900009,'2026-03-12 10:00:00',NULL,'2026-03-11 10:00:00.000','2026-03-12 10:00:00.000',900002,900009,0),(840110,800004,900008,'对摄影构图和后期很感兴趣','会用Lightroom',2,900009,'2026-03-15 10:00:00',NULL,'2026-03-14 10:00:00.000','2026-03-15 10:00:00.000',900008,900009,0),(840111,286387520438272,900006,'对传统文化很感兴趣','读过很多文学作品',2,2003,'2026-04-01 10:00:00',NULL,'2026-03-31 10:00:00.000','2026-04-01 10:00:00.000',900006,2003,0),(286394933559296,286387520438272,285158391963648,'test3入社理由','test3个人简介',3,2003,'2026-03-21 17:49:16','test3再次驳回','2026-03-20 14:23:58.077','2026-03-21 17:49:16.140',285158391963648,2003,0),(286795914035200,286387520438272,285158650445824,'test入社理由','test个人简介',2,2003,'2026-03-21 17:49:19',NULL,'2026-03-21 17:35:33.699','2026-03-21 17:49:18.741',285158650445824,2003,0),(288680539918336,286387520438272,285095129079808,'test2的入社理由','test2申请的个人简介',2,2003,'2026-03-27 01:24:21',NULL,'2026-03-27 01:24:07.440','2026-03-27 01:24:21.371',285095129079808,2003,0),(295240677662720,295239663321088,285158391963648,'入社理由','个人简介',2,295239277260800,'2026-04-14 14:18:03',NULL,'2026-04-14 14:17:23.569','2026-04-14 14:18:02.794',285158391963648,295239277260800,0);
/*!40000 ALTER TABLE `club_join_apply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club_member`
--

DROP TABLE IF EXISTS `club_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club_member` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `position_id` bigint DEFAULT NULL,
  `join_at` datetime NOT NULL,
  `quit_at` datetime DEFAULT NULL,
  `member_status` tinyint NOT NULL DEFAULT '1' COMMENT '1在籍 2已退出',
  `contribution_score` decimal(10,2) NOT NULL DEFAULT '0.00',
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_club_user` (`club_id`,`user_id`),
  KEY `idx_member_status` (`member_status`),
  KEY `fk_club_member_user` (`user_id`),
  CONSTRAINT `fk_club_member_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`),
  CONSTRAINT `fk_club_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club_member`
--

LOCK TABLES `club_member` WRITE;
/*!40000 ALTER TABLE `club_member` DISABLE KEYS */;
INSERT INTO `club_member` VALUES (830001,800001,900001,820002,'2026-03-10 10:00:00',NULL,1,15.00,'2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(830002,800001,900002,NULL,'2026-03-10 10:00:00',NULL,1,5.00,'2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(830003,800001,900003,820003,'2026-03-10 10:00:00',NULL,1,20.00,'2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(830004,800001,900004,NULL,'2026-03-12 10:00:00',NULL,1,0.00,'2026-03-12 10:00:00.000','2026-03-12 10:00:00.000',900009,900009,0),(830005,800002,900005,820005,'2026-03-10 10:00:00',NULL,1,30.00,'2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900010,900010,0),(830006,800002,900006,NULL,'2026-03-11 10:00:00',NULL,1,10.00,'2026-03-11 10:00:00.000','2026-03-11 10:00:00.000',900010,900010,0),(830007,800002,900007,NULL,'2026-03-12 10:00:00',NULL,1,5.00,'2026-03-12 10:00:00.000','2026-03-12 10:00:00.000',900010,900010,0),(830008,800003,900001,820007,'2026-03-10 10:00:00',NULL,1,10.00,'2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(830009,800003,900005,NULL,'2026-03-11 10:00:00',NULL,1,5.00,'2026-03-11 10:00:00.000','2026-03-11 10:00:00.000',900009,900009,0),(830010,800004,900002,820009,'2026-03-12 10:00:00',NULL,1,8.00,'2026-03-12 10:00:00.000','2026-03-12 10:00:00.000',900009,900009,0),(830011,800004,900008,NULL,'2026-03-15 10:00:00',NULL,1,0.00,'2026-03-15 10:00:00.000','2026-03-15 10:00:00.000',900009,900009,0),(830012,286387520438272,900006,NULL,'2026-04-01 10:00:00',NULL,1,0.00,'2026-04-01 10:00:00.000','2026-04-01 10:00:00.000',2003,2003,0),(286394859360256,286387520438272,285095129079808,286395084427264,'2026-03-27 01:24:21',NULL,1,0.00,'2026-03-20 14:23:39.959','2026-03-27 01:38:24.416',2003,2003,0),(286799293411328,286387520438272,285158650445824,NULL,'2026-03-21 17:49:19',NULL,1,0.00,'2026-03-21 17:49:18.741','2026-03-21 17:49:18.741',2003,2003,0),(295240838356992,295239663321088,285158391963648,295241013305344,'2026-04-14 14:18:03',NULL,1,0.00,'2026-04-14 14:18:02.794','2026-04-14 14:20:14.724',295239277260800,295239277260800,0);
/*!40000 ALTER TABLE `club_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club_position`
--

DROP TABLE IF EXISTS `club_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club_position` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `position_name` varchar(50) NOT NULL,
  `parent_position_id` bigint DEFAULT NULL,
  `level_no` int NOT NULL,
  `sort_no` int NOT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_club_level` (`club_id`,`level_no`),
  CONSTRAINT `fk_club_position_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club_position`
--

LOCK TABLES `club_position` WRITE;
/*!40000 ALTER TABLE `club_position` DISABLE KEYS */;
INSERT INTO `club_position` VALUES (820001,800001,'社长',NULL,1,1,'2026-03-05 10:00:00.000','2026-03-05 10:00:00.000',900009,900009,0),(820002,800001,'副社长',820001,2,1,'2026-03-05 10:00:00.000','2026-03-05 10:00:00.000',900009,900009,0),(820003,800001,'队长',820001,2,2,'2026-03-05 10:00:00.000','2026-03-05 10:00:00.000',900009,900009,0),(820004,800002,'社长',NULL,1,1,'2026-03-05 10:00:00.000','2026-03-05 10:00:00.000',900010,900010,0),(820005,800002,'技术部长',820004,2,1,'2026-03-05 10:00:00.000','2026-03-05 10:00:00.000',900010,900010,0),(820006,800003,'社长',NULL,1,1,'2026-03-05 10:00:00.000','2026-03-05 10:00:00.000',900009,900009,0),(820007,800003,'演出部长',820006,2,1,'2026-03-05 10:00:00.000','2026-03-05 10:00:00.000',900009,900009,0),(820008,800004,'会长',NULL,1,1,'2026-03-05 10:00:00.000','2026-03-05 10:00:00.000',900009,900009,0),(820009,800004,'外联部长',820008,2,1,'2026-03-05 10:00:00.000','2026-03-05 10:00:00.000',900009,900009,0),(286395051233280,286387520438272,'部长',NULL,1,10,'2026-03-20 14:24:26.805','2026-03-20 14:24:26.805',2003,2003,0),(286395084427264,286387520438272,'副部长',286395051233280,2,10,'2026-03-20 14:24:34.909','2026-03-20 14:24:34.909',2003,2003,0),(295240979046400,295239663321088,'部长',NULL,1,10,'2026-04-14 14:18:37.151','2026-04-14 14:18:37.151',295239277260800,295239277260800,0),(295241013305344,295239663321088,'副部长',295240979046400,2,10,'2026-04-14 14:18:45.514','2026-04-14 14:18:45.514',295239277260800,295239277260800,0);
/*!40000 ALTER TABLE `club_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `club_review`
--

DROP TABLE IF EXISTS `club_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `club_review` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `review_year` int NOT NULL,
  `summary_text` text COMMENT '年度工作总结',
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '补充材料附件URL',
  `total_income` decimal(14,2) DEFAULT '0.00' COMMENT '年度总收入（快照）',
  `total_expense` decimal(14,2) DEFAULT '0.00' COMMENT '年度总支出（快照）',
  `balance` decimal(14,2) DEFAULT '0.00' COMMENT '当前余额（快照）',
  `member_count` int DEFAULT '0' COMMENT '在籍成员数（快照）',
  `event_count` int DEFAULT '0' COMMENT '年度活动数（快照）',
  `report_url` varchar(255) DEFAULT NULL,
  `finance_report_url` varchar(255) DEFAULT NULL,
  `review_status` tinyint NOT NULL COMMENT '1待提交 2待审核 3通过 4驳回 5整改中',
  `score` decimal(5,2) DEFAULT NULL,
  `reject_reason` varchar(500) DEFAULT NULL,
  `reviewed_by` bigint DEFAULT NULL COMMENT '审核人',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_club_year` (`club_id`,`review_year`),
  KEY `idx_review_status` (`review_status`),
  CONSTRAINT `fk_club_review_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club_review`
--

LOCK TABLES `club_review` WRITE;
/*!40000 ALTER TABLE `club_review` DISABLE KEYS */;
INSERT INTO `club_review` VALUES (295417550209024,800001,2026,NULL,NULL,0.00,0.00,0.00,0,0,NULL,NULL,1,NULL,NULL,NULL,NULL,'2026-04-15 02:17:05.343','2026-04-15 02:17:05.343',2001,2001,0),(295417550221312,800004,2026,NULL,NULL,0.00,0.00,0.00,0,0,NULL,NULL,1,NULL,NULL,NULL,NULL,'2026-04-15 02:17:05.343','2026-04-15 02:17:05.343',2001,2001,0),(295417550229504,286387520438272,2026,NULL,NULL,0.00,0.00,0.00,0,0,NULL,NULL,1,NULL,NULL,NULL,NULL,'2026-04-15 02:17:05.343','2026-04-15 02:17:05.343',2001,2001,0),(295417550245888,295239663321088,2026,'test','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/82436bdc0ede447d89de08227969f9ee.docx',1000.00,600.00,400.00,1,1,NULL,NULL,3,5.00,NULL,2001,'2026-04-15 02:20:09','2026-04-15 02:17:05.343','2026-04-15 02:20:08.952',2001,295239277260800,0),(295417550258176,800003,2026,NULL,NULL,0.00,0.00,0.00,0,0,NULL,NULL,1,NULL,NULL,NULL,NULL,'2026-04-15 02:17:05.343','2026-04-15 02:17:05.343',2001,2001,0),(295417550274560,800002,2026,NULL,NULL,0.00,0.00,0.00,0,0,NULL,NULL,1,NULL,NULL,NULL,NULL,'2026-04-15 02:17:05.343','2026-04-15 02:17:05.343',2001,2001,0);
/*!40000 ALTER TABLE `club_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `title` varchar(200) NOT NULL,
  `content` text,
  `location` varchar(200) DEFAULT NULL,
  `start_at` datetime NOT NULL,
  `end_at` datetime NOT NULL,
  `signup_start_at` datetime DEFAULT NULL,
  `signup_end_at` datetime DEFAULT NULL,
  `limit_count` int NOT NULL DEFAULT '0',
  `only_member` tinyint(1) NOT NULL DEFAULT '0',
  `event_status` tinyint NOT NULL COMMENT '1草稿 2待审核 3报名中 4已结束 5驳回 6进行中',
  `safety_plan_url` varchar(255) DEFAULT NULL,
  `checkin_code` varchar(20) DEFAULT NULL COMMENT '签到码',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因',
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_club_time` (`club_id`,`start_at`),
  KEY `idx_event_status` (`event_status`),
  CONSTRAINT `fk_event_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (850001,800001,'春季篮球联赛','一年一度的春季篮球联赛，分组对抗赛制，欢迎全校同学报名参加！','体育馆A','2026-04-20 14:00:00','2026-04-20 18:00:00','2026-04-10 00:00:00','2026-04-19 23:59:59',50,0,3,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1e6cbce1892b43428bb859890d971550.docx','BASK2026',NULL,'2026-04-08 10:00:00.000','2026-04-10 10:00:00.000',900009,2001,0),(850002,800001,'新生篮球训练营','针对篮球新手的基础训练营，包含运球、投篮、战术配合等内容。','篮球场','2026-04-05 09:00:00','2026-04-30 17:00:00','2026-04-01 00:00:00','2026-04-04 23:59:59',30,1,6,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1e6cbce1892b43428bb859890d971550.docx','CAMP2026',NULL,'2026-03-28 10:00:00.000','2026-04-05 09:00:00.000',900009,2001,0),(850003,800002,'算法竞赛培训','备战ACM校赛，由往届获奖选手分享经验，刷题训练。','计算机楼301','2026-04-25 19:00:00','2026-04-25 21:00:00','2026-04-10 00:00:00','2026-04-24 23:59:59',100,0,3,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1e6cbce1892b43428bb859890d971550.docx','ACM2026',NULL,'2026-04-08 10:00:00.000','2026-04-10 10:00:00.000',900010,2001,0),(850004,800002,'Python入门工作坊','面向零基础同学的Python编程入门课程，包含实战项目。','计算机楼201','2026-05-10 14:00:00','2026-05-10 17:00:00','2026-05-01 00:00:00','2026-05-09 23:59:59',40,0,2,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1e6cbce1892b43428bb859890d971550.docx',NULL,NULL,'2026-04-09 10:00:00.000','2026-04-09 10:00:00.000',900010,900010,0),(850005,800003,'校园民谣演唱会','学期末民谣演唱会，社员自弹自唱表演，全校师生均可观看。','小礼堂','2026-04-28 19:00:00','2026-04-28 21:30:00','2026-04-10 00:00:00','2026-04-27 23:59:59',200,0,3,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1e6cbce1892b43428bb859890d971550.docx','FOLK2026',NULL,'2026-04-08 10:00:00.000','2026-04-10 10:00:00.000',900009,2001,0),(850006,800004,'春日摄影大赛','以\"春天\"为主题的摄影比赛，设一等奖1名、二等奖3名、三等奖5名。','线上提交','2026-03-20 00:00:00','2026-04-05 23:59:59','2026-03-15 00:00:00','2026-03-19 23:59:59',100,0,4,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1e6cbce1892b43428bb859890d971550.docx','PHOTO026',NULL,'2026-03-10 10:00:00.000','2026-04-06 00:00:00.000',900009,2001,0),(850007,286387520438272,'文化沙龙第二期','本期主题：传统文化在当代的传承与创新，欢迎社团成员参与讨论。','人文学院报告厅','2026-04-22 15:00:00','2026-04-22 17:00:00','2026-04-10 00:00:00','2026-04-21 23:59:59',20,1,3,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1e6cbce1892b43428bb859890d971550.docx','SALON02',NULL,'2026-04-08 10:00:00.000','2026-04-10 10:00:00.000',2003,2001,0),(850008,286387520438272,'读书分享会','每人分享一本近期读过的好书，交流心得体会。','图书馆二楼','2026-05-15 14:00:00','2026-05-15 16:00:00','2026-05-05 00:00:00','2026-05-14 23:59:59',30,0,2,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1e6cbce1892b43428bb859890d971550.docx',NULL,NULL,'2026-04-10 10:00:00.000','2026-04-10 10:00:00.000',2003,2003,0),(288870377975808,286387520438272,'test1活动','test1活动内容','操场','2026-03-27 14:17:00','2026-03-27 14:20:00','2026-03-27 14:16:00','2026-03-27 14:20:00',120,1,4,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1e6cbce1892b43428bb859890d971550.docx','1234',NULL,'2026-03-27 14:16:34.624','2026-03-27 14:22:00.002',2003,2001,0),(292777651609600,286387520438272,'test2','test2的活动内容','操场','2026-04-08 15:15:00','2026-04-08 16:15:00','2026-04-07 15:10:00','2026-04-08 15:10:00',10,1,4,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/75fc851acc884c809a1a82c3504f1c90.docx',NULL,NULL,'2026-04-07 15:15:18.851','2026-04-11 20:21:43.858',2003,2001,0),(295241742966784,295239663321088,'活动1','内容','操场','2026-04-14 14:25:00','2026-04-14 14:35:00','2026-04-14 14:21:00','2026-04-14 14:25:00',120,1,4,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/88a16783207449b6a1ed69a2447073da.docx','1234',NULL,'2026-04-14 14:21:43.655','2026-04-14 14:35:00.009',295239277260800,2001,0),(296025245392896,286387520438272,'测试活动','测试签到功能','操场','2026-04-16 20:00:00','2026-04-16 21:29:00','2026-04-16 19:29:00','2026-04-16 20:00:00',120,1,4,'https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/77551ca8916d4b598fc5b7a5d6ed7086.docx','1234',NULL,'2026-04-16 19:29:48.427','2026-04-17 09:35:48.773',2003,2001,0);
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_checkin`
--

DROP TABLE IF EXISTS `event_checkin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_checkin` (
  `id` bigint NOT NULL,
  `event_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `checkin_type` tinyint NOT NULL COMMENT '1校园码 2人脸',
  `checkin_at` datetime NOT NULL,
  `device_no` varchar(64) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_event_checkin` (`event_id`,`checkin_at`),
  KEY `fk_event_checkin_user` (`user_id`),
  CONSTRAINT `fk_event_checkin_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `fk_event_checkin_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_checkin`
--

LOCK TABLES `event_checkin` WRITE;
/*!40000 ALTER TABLE `event_checkin` DISABLE KEYS */;
INSERT INTO `event_checkin` VALUES (288871247495168,288870377975808,285095129079808,1,'2026-03-27 14:20:21',NULL,'2026-03-27 14:20:06.907','2026-03-27 14:20:20.986',285095129079808,2003,0),(295242027220992,295241742966784,285158391963648,1,'2026-04-14 14:23:35',NULL,'2026-04-14 14:22:53.051','2026-04-14 14:23:35.037',295239277260800,295239277260800,0),(296025968857088,296025245392896,285095129079808,1,'2026-04-16 20:37:44',NULL,'2026-04-16 19:32:45.051','2026-04-16 20:37:44.399',2003,285095129079808,0);
/*!40000 ALTER TABLE `event_checkin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_signup`
--

DROP TABLE IF EXISTS `event_signup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_signup` (
  `id` bigint NOT NULL,
  `event_id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `signup_status` tinyint NOT NULL DEFAULT '1' COMMENT '1已报名 2取消',
  `signup_at` datetime NOT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_user` (`event_id`,`user_id`),
  KEY `idx_event_signup` (`event_id`,`signup_status`),
  KEY `fk_event_signup_user` (`user_id`),
  CONSTRAINT `fk_event_signup_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `fk_event_signup_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_signup`
--

LOCK TABLES `event_signup` WRITE;
/*!40000 ALTER TABLE `event_signup` DISABLE KEYS */;
INSERT INTO `event_signup` VALUES (860001,850001,800001,900001,1,'2026-04-10 10:30:00','2026-04-10 10:30:00.000','2026-04-10 10:30:00.000',NULL,NULL,0),(860002,850001,800001,900002,1,'2026-04-10 10:35:00','2026-04-10 10:35:00.000','2026-04-10 10:35:00.000',NULL,NULL,0),(860003,850001,800001,900003,1,'2026-04-10 11:00:00','2026-04-10 11:00:00.000','2026-04-10 11:00:00.000',NULL,NULL,0),(860004,850001,800001,900004,1,'2026-04-10 11:20:00','2026-04-10 11:20:00.000','2026-04-10 11:20:00.000',NULL,NULL,0),(860005,850001,800001,900006,2,'2026-04-10 12:00:00','2026-04-10 12:00:00.000','2026-04-10 13:00:00.000',NULL,NULL,0),(860006,850002,800001,900001,1,'2026-04-01 10:00:00','2026-04-01 10:00:00.000','2026-04-01 10:00:00.000',NULL,NULL,0),(860007,850002,800001,900003,1,'2026-04-01 10:30:00','2026-04-01 10:30:00.000','2026-04-01 10:30:00.000',NULL,NULL,0),(860008,850002,800001,900004,1,'2026-04-02 09:00:00','2026-04-02 09:00:00.000','2026-04-02 09:00:00.000',NULL,NULL,0),(860009,850003,800002,900005,1,'2026-04-10 14:00:00','2026-04-10 14:00:00.000','2026-04-10 14:00:00.000',NULL,NULL,0),(860010,850003,800002,900006,1,'2026-04-10 14:30:00','2026-04-10 14:30:00.000','2026-04-10 14:30:00.000',NULL,NULL,0),(860011,850003,800002,900001,1,'2026-04-10 15:00:00','2026-04-10 15:00:00.000','2026-04-10 15:00:00.000',NULL,NULL,0),(860012,850005,800003,900001,1,'2026-04-11 10:00:00','2026-04-11 10:00:00.000','2026-04-11 10:00:00.000',NULL,NULL,0),(860013,850005,800003,900008,1,'2026-04-11 11:00:00','2026-04-11 11:00:00.000','2026-04-11 11:00:00.000',NULL,NULL,0),(860014,850007,286387520438272,285095129079808,1,'2026-04-10 16:00:00','2026-04-10 16:00:00.000','2026-04-10 16:00:00.000',NULL,NULL,0),(860015,850007,286387520438272,900006,1,'2026-04-10 16:30:00','2026-04-10 16:30:00.000','2026-04-10 16:30:00.000',NULL,NULL,0),(288870518558720,288870377975808,286387520438272,285095129079808,1,'2026-03-27 14:17:09','2026-03-27 14:17:08.944','2026-03-27 14:17:08.944',NULL,NULL,0),(288870716776448,288870377975808,286387520438272,285158650445824,2,'2026-03-27 14:17:57','2026-03-27 14:17:57.336','2026-03-27 14:17:58.236',NULL,NULL,0),(295241909141504,295241742966784,295239663321088,285158391963648,1,'2026-04-14 14:22:24','2026-04-14 14:22:24.216','2026-04-14 14:22:24.216',NULL,NULL,0),(296025455112192,296025245392896,286387520438272,285095129079808,1,'2026-04-16 19:30:40','2026-04-16 19:30:39.609','2026-04-16 20:01:51.515',NULL,NULL,0);
/*!40000 ALTER TABLE `event_signup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_summary`
--

DROP TABLE IF EXISTS `event_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_summary` (
  `id` bigint NOT NULL,
  `event_id` bigint NOT NULL,
  `summary_text` text,
  `summary_images` text COMMENT '总结图片URL数组(JSON格式)',
  `feedback_score` decimal(4,2) DEFAULT NULL,
  `issue_reflection` text,
  `attachment_url` varchar(255) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_summary` (`event_id`),
  CONSTRAINT `fk_event_summary_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_summary`
--

LOCK TABLES `event_summary` WRITE;
/*!40000 ALTER TABLE `event_summary` DISABLE KEYS */;
INSERT INTO `event_summary` VALUES (870001,850006,'春日摄影大赛圆满结束，共收到86份参赛作品，经专业评审评选出一等奖1名、二等奖3名、三等奖5名。',NULL,4.50,'部分同学提交作品格式不符合要求，下次需提前明确格式规范。',NULL,'2026-04-06 10:00:00.000','2026-04-06 10:00:00.000',900009,900009,0),(288872056872960,288870377975808,'test1总结','[\"https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/2dab16bdb3f645709baadf849fb2917f.jpg\",\"https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/c64d5e8a00a949009eee68d75d83846a.jpg\"]',5.00,'test1问题反思','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/00e15580ac62468d80e1e65c6c6dfe8a.docx','2026-03-27 14:23:24.508','2026-03-27 14:23:30.271',2003,2003,0);
/*!40000 ALTER TABLE `event_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expense`
--

DROP TABLE IF EXISTS `expense`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expense` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `event_id` bigint DEFAULT NULL,
  `budget_id` bigint DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `amount` decimal(14,2) NOT NULL,
  `payee_name` varchar(100) DEFAULT NULL,
  `payee_account` varchar(100) DEFAULT NULL,
  `invoice_url` varchar(255) DEFAULT NULL,
  `expense_desc` varchar(500) DEFAULT NULL,
  `approve_level` tinyint NOT NULL COMMENT '1社团自审 2学校审核',
  `expense_status` tinyint NOT NULL COMMENT '1待审 2通过 3驳回 4已支付',
  `reject_reason` varchar(500) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_expense_club_status` (`club_id`,`expense_status`),
  KEY `idx_expense_amount` (`amount`),
  KEY `fk_expense_event` (`event_id`),
  KEY `fk_expense_budget` (`budget_id`),
  CONSTRAINT `fk_expense_budget` FOREIGN KEY (`budget_id`) REFERENCES `budget` (`id`),
  CONSTRAINT `fk_expense_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`),
  CONSTRAINT `fk_expense_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expense`
--

LOCK TABLES `expense` WRITE;
/*!40000 ALTER TABLE `expense` DISABLE KEYS */;
INSERT INTO `expense` VALUES (890001,800001,NULL,NULL,'物料采购',300.00,'体育用品店','6228000001','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/5f14806f36a04f078475de855b0921b8.docx','购买篮球10个',1,2,NULL,'2026-03-15 10:00:00.000','2026-03-15 10:00:00.000',900009,900009,0),(890002,800001,NULL,NULL,'场地租赁',800.00,'体育馆管理处','6228000002','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/5f14806f36a04f078475de855b0921b8.docx','租赁体育馆场地',2,2,NULL,'2026-03-20 10:00:00.000','2026-03-20 12:00:00.000',900009,2001,0),(890003,800001,NULL,NULL,'物料采购',1200.00,'运动装备店','6228000003','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/5f14806f36a04f078475de855b0921b8.docx','购买队服和训练器材',2,2,NULL,'2026-04-05 10:00:00.000','2026-04-15 02:35:26.172',900009,2001,0),(890004,800002,NULL,NULL,'物料采购',200.00,'书店','6228000004','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/5f14806f36a04f078475de855b0921b8.docx','购买算法竞赛参考书',1,2,NULL,'2026-03-20 10:00:00.000','2026-03-20 10:00:00.000',900010,900010,0),(890005,800002,NULL,NULL,'服务器租赁',600.00,'云服务商','6228000005','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/5f14806f36a04f078475de855b0921b8.docx','租用云服务器一年',2,3,'请提供更详细的用途说明','2026-03-25 10:00:00.000','2026-03-28 10:00:00.000',900010,2001,0),(890006,800003,NULL,NULL,'物料采购',450.00,'乐器店','6228000006','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/5f14806f36a04f078475de855b0921b8.docx','购买吉他弦和拨片',1,2,NULL,'2026-03-15 10:00:00.000','2026-03-15 10:00:00.000',900009,900009,0),(890007,800004,NULL,NULL,'物料采购',350.00,'数码店','6228000007','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/5f14806f36a04f078475de855b0921b8.docx','购买存储卡和电池',1,2,NULL,'2026-03-20 10:00:00.000','2026-03-20 10:00:00.000',900009,900009,0),(890008,800004,NULL,NULL,'活动费用',900.00,'印刷厂','6228000008','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/5f14806f36a04f078475de855b0921b8.docx','摄影作品展板制作费用',2,2,NULL,'2026-04-02 10:00:00.000','2026-04-03 10:00:00.000',900009,2001,0),(292759669972992,286387520438272,NULL,NULL,'场地租赁',100.00,'张','1111','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/5f14806f36a04f078475de855b0921b8.docx','111',1,2,NULL,'2026-04-07 14:02:08.803','2026-04-07 14:02:08.803',2003,2003,0),(292759744417792,286387520438272,NULL,NULL,'物料采购',600.00,'张','1111','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/12056a9a639b4e52a04f630281aaa18f.docx','111',2,2,NULL,'2026-04-07 14:02:26.977','2026-04-07 14:02:38.325',2003,2001,0),(295242849251328,295239663321088,NULL,NULL,'场地租赁',600.00,'张老师','10010','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/e9601a4ccba245ba9af563ac7cd7ebca.docx','支出',2,2,NULL,'2026-04-14 14:26:13.744','2026-04-14 14:26:26.313',295239277260800,2001,0),(295422909378560,800001,NULL,NULL,'场地租赁',1500.00,'李老师','10101100','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/b00de0a299c3452ab82b00488d90d135.docx','租赁篮球场地',2,1,NULL,'2026-04-15 02:38:53.735','2026-04-15 02:38:53.735',900009,900009,0);
/*!40000 ALTER TABLE `expense` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flow`
--

DROP TABLE IF EXISTS `flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flow` (
  `id` bigint NOT NULL,
  `biz_type` varchar(30) NOT NULL COMMENT 'CLUB_APPLY/CLUB_REVIEW/EXPENSE_APPROVE',
  `biz_id` bigint NOT NULL,
  `process_key` varchar(50) NOT NULL,
  `current_node` varchar(50) DEFAULT NULL,
  `flow_status` tinyint NOT NULL COMMENT '1进行中 2通过 3驳回 4终止',
  `start_user_id` bigint DEFAULT NULL,
  `finished_at` datetime DEFAULT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_flow_biz` (`biz_type`,`biz_id`),
  KEY `idx_flow_status` (`flow_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flow`
--

LOCK TABLES `flow` WRITE;
/*!40000 ALTER TABLE `flow` DISABLE KEYS */;
/*!40000 ALTER TABLE `flow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flow_task`
--

DROP TABLE IF EXISTS `flow_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flow_task` (
  `id` bigint NOT NULL,
  `flow_id` bigint NOT NULL,
  `node_code` varchar(50) NOT NULL,
  `assignee_user_id` bigint DEFAULT NULL,
  `task_status` tinyint NOT NULL COMMENT '1待处理 2已通过 3已驳回 4已转交',
  `opinion` varchar(500) DEFAULT NULL,
  `handled_at` datetime DEFAULT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_flow_task_status` (`flow_id`,`task_status`),
  CONSTRAINT `fk_flow_task_flow` FOREIGN KEY (`flow_id`) REFERENCES `flow` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flow_task`
--

LOCK TABLES `flow_task` WRITE;
/*!40000 ALTER TABLE `flow_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `flow_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `income`
--

DROP TABLE IF EXISTS `income`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `income` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `income_type` tinyint NOT NULL COMMENT '1学校拨款 2赞助 3会费',
  `amount` decimal(14,2) NOT NULL,
  `occur_at` datetime NOT NULL,
  `proof_url` varchar(255) DEFAULT NULL,
  `source_desc` varchar(255) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_income_club_time` (`club_id`,`occur_at`),
  CONSTRAINT `fk_income_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `income`
--

LOCK TABLES `income` WRITE;
/*!40000 ALTER TABLE `income` DISABLE KEYS */;
INSERT INTO `income` VALUES (880001,800001,1,3000.00,'2026-03-10 10:00:00',NULL,'学校体育活动专项拨款','2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(880002,800001,2,2000.00,'2026-03-20 10:00:00',NULL,'某体育品牌赞助','2026-03-20 10:00:00.000','2026-03-20 10:00:00.000',900009,900009,0),(880003,800002,1,2000.00,'2026-03-10 10:00:00',NULL,'学校科技社团经费','2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900010,900010,0),(880004,800002,3,500.00,'2026-03-15 10:00:00',NULL,'社员会费','2026-03-15 10:00:00.000','2026-03-15 10:00:00.000',900010,900010,0),(880005,800003,1,1500.00,'2026-03-10 10:00:00',NULL,'学校文艺活动拨款','2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(880006,800003,3,800.00,'2026-03-20 10:00:00',NULL,'社员会费','2026-03-20 10:00:00.000','2026-03-20 10:00:00.000',900009,900009,0),(880007,800004,1,2500.00,'2026-03-10 10:00:00',NULL,'学校社团拨款','2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(880008,800004,2,1000.00,'2026-04-01 10:00:00',NULL,'某相机品牌赞助','2026-04-01 10:00:00.000','2026-04-01 10:00:00.000',900009,900009,0),(292759551733760,286387520438272,1,1000.00,'2026-04-07 14:00:00','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/23aa099783684a46a2b6297b1cfa2187.docx','测试收入','2026-04-07 14:01:39.935','2026-04-07 14:01:39.935',2003,2003,0),(295242544041984,295239663321088,1,1000.00,'2026-04-14 14:24:00','https://club-1402678222.cos.ap-beijing.myqcloud.com/uploads/1732f45d276e4901802269832aa1bc61.docx','11','2026-04-14 14:24:59.229','2026-04-14 14:24:59.229',295239277260800,295239277260800,0);
/*!40000 ALTER TABLE `income` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ledger`
--

DROP TABLE IF EXISTS `ledger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ledger` (
  `id` bigint NOT NULL,
  `club_id` bigint NOT NULL,
  `biz_type` tinyint NOT NULL COMMENT '1收入 2支出',
  `biz_id` bigint NOT NULL,
  `change_amount` decimal(14,2) NOT NULL,
  `balance_after` decimal(14,2) NOT NULL,
  `occur_at` datetime NOT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_ledger_club_time` (`club_id`,`occur_at`),
  CONSTRAINT `fk_ledger_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ledger`
--

LOCK TABLES `ledger` WRITE;
/*!40000 ALTER TABLE `ledger` DISABLE KEYS */;
INSERT INTO `ledger` VALUES (895001,800001,1,880001,3000.00,3000.00,'2026-03-10 10:00:00','2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(895002,800001,2,890001,-300.00,2700.00,'2026-03-15 10:00:00','2026-03-15 10:00:00.000','2026-03-15 10:00:00.000',900009,900009,0),(895003,800001,1,880002,2000.00,4700.00,'2026-03-20 10:00:00','2026-03-20 10:00:00.000','2026-03-20 10:00:00.000',900009,900009,0),(895004,800001,2,890002,-800.00,3900.00,'2026-03-20 12:00:00','2026-03-20 12:00:00.000','2026-03-20 12:00:00.000',2001,2001,0),(895005,800002,1,880003,2000.00,2000.00,'2026-03-10 10:00:00','2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900010,900010,0),(895006,800002,1,880004,500.00,2500.00,'2026-03-15 10:00:00','2026-03-15 10:00:00.000','2026-03-15 10:00:00.000',900010,900010,0),(895007,800002,2,890004,-200.00,2300.00,'2026-03-20 10:00:00','2026-03-20 10:00:00.000','2026-03-20 10:00:00.000',900010,900010,0),(895008,800003,1,880005,1500.00,1500.00,'2026-03-10 10:00:00','2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(895009,800003,2,890006,-450.00,1050.00,'2026-03-15 10:00:00','2026-03-15 10:00:00.000','2026-03-15 10:00:00.000',900009,900009,0),(895010,800003,1,880006,800.00,1850.00,'2026-03-20 10:00:00','2026-03-20 10:00:00.000','2026-03-20 10:00:00.000',900009,900009,0),(895011,800004,1,880007,2500.00,2500.00,'2026-03-10 10:00:00','2026-03-10 10:00:00.000','2026-03-10 10:00:00.000',900009,900009,0),(895012,800004,2,890007,-350.00,2150.00,'2026-03-20 10:00:00','2026-03-20 10:00:00.000','2026-03-20 10:00:00.000',900009,900009,0),(895013,800004,1,880008,1000.00,3150.00,'2026-04-01 10:00:00','2026-04-01 10:00:00.000','2026-04-01 10:00:00.000',900009,900009,0),(895014,800004,2,890008,-900.00,2250.00,'2026-04-03 10:00:00','2026-04-03 10:00:00.000','2026-04-03 10:00:00.000',2001,2001,0),(292759551741952,286387520438272,1,292759551733760,1000.00,1000.00,'2026-04-07 14:00:00','2026-04-07 14:01:39.935','2026-04-07 14:01:39.935',2003,2003,0),(292759669972993,286387520438272,2,292759669972992,-100.00,900.00,'2026-04-07 14:02:09','2026-04-07 14:02:08.803','2026-04-07 14:02:08.803',2003,2003,0),(292759790911488,286387520438272,2,292759744417792,-600.00,300.00,'2026-04-07 14:02:38','2026-04-07 14:02:38.325','2026-04-07 14:02:38.325',2001,2001,0),(295242544050176,295239663321088,1,295242544041984,1000.00,1000.00,'2026-04-14 14:24:00','2026-04-14 14:24:59.229','2026-04-14 14:24:59.229',295239277260800,295239277260800,0),(295242900746240,295239663321088,2,295242849251328,-600.00,400.00,'2026-04-14 14:26:26','2026-04-14 14:26:26.313','2026-04-14 14:26:26.313',2001,2001,0),(295422059212800,800001,2,890003,-1200.00,2700.00,'2026-04-15 02:35:26','2026-04-15 02:35:26.172','2026-04-15 02:35:26.172',2001,2001,0);
/*!40000 ALTER TABLE `ledger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL,
  `role_code` varchar(30) NOT NULL COMMENT 'STUDENT/CLUB_ADMIN/SCHOOL_ADMIN',
  `role_name` varchar(50) NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1001,'STUDENT','学生',1,'2026-03-11 03:32:34.685','2026-03-11 03:44:32.310',0,0,0),(1002,'CLUB_ADMIN','社团管理员',1,'2026-03-11 03:32:34.685','2026-03-11 03:44:32.310',0,0,0),(1003,'SCHOOL_ADMIN','学校管理员',1,'2026-03-11 03:32:34.685','2026-03-11 03:44:32.310',0,0,0);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `real_name` varchar(50) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1启用 2冻结 3注销',
  `student_no` varchar(30) DEFAULT NULL,
  `staff_no` varchar(30) DEFAULT NULL,
  `graduate_at` datetime DEFAULT NULL,
  `last_login_at` datetime DEFAULT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_student_no` (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2001,'admin','$2a$10$Kf70YJ67JRGN2YwwePtcGO7oi178hy58FEKXY7VuFhezYxCpfP6HC','系统管理员',NULL,NULL,1,NULL,NULL,NULL,'2026-04-16 19:34:40','2026-03-11 03:44:32.311','2026-04-16 19:34:40.339',0,0,0),(2002,'cadmin','$2a$10$9cCExfatS1klseFP0QA8RuJhcx.ZxIH4uT1KR4qGOZtwqamBuHx7W','社团管理员',NULL,NULL,1,NULL,NULL,NULL,'2026-03-11 22:03:52','2026-03-11 03:44:32.311','2026-03-11 22:03:52.084',0,0,0),(2003,'test','$2a$10$TB9Ki4/vC7LdkE5SM1v9GurWfLRtSXp/oKQu9EF8hjtkzP291jYG.','测试学生',NULL,NULL,1,NULL,NULL,NULL,'2026-04-16 20:36:59','2026-03-11 03:44:32.311','2026-04-16 20:36:58.974',0,0,0),(900001,'student01','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','李明','13800000001','liming@bistu.edu.cn',1,'2023011001',NULL,NULL,NULL,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(900002,'student02','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','王芳','13800000002','wangfang@bistu.edu.cn',1,'2023011002',NULL,NULL,NULL,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(900003,'student03','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','张伟','13800000003',NULL,1,'2023011003',NULL,NULL,'2026-04-11 20:34:16','2026-03-01 10:00:00.000','2026-04-11 20:34:15.697',NULL,NULL,0),(900004,'student04','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','刘洋','13800000004',NULL,1,'2023011004',NULL,NULL,NULL,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(900005,'student05','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','陈静','13800000005','chenjing@bistu.edu.cn',1,'2023011005',NULL,NULL,'2026-04-14 20:09:13','2026-03-01 10:00:00.000','2026-04-14 20:09:13.397',NULL,NULL,0),(900006,'student06','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','杨帆','13800000006',NULL,1,'2023011006',NULL,NULL,'2026-04-11 20:49:44','2026-03-01 10:00:00.000','2026-04-11 20:49:43.798',NULL,NULL,0),(900007,'student07','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','赵磊','13800000007',NULL,1,'2023011007',NULL,NULL,'2026-04-15 02:37:42','2026-03-01 10:00:00.000','2026-04-15 02:37:41.747',NULL,NULL,0),(900008,'student08','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','孙悦','13800000008','sunyue@bistu.edu.cn',1,'2023011008',NULL,NULL,NULL,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(900009,'cadmin2','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','周强','13800000009',NULL,1,'2022011009',NULL,NULL,'2026-04-15 02:38:16','2026-03-01 10:00:00.000','2026-04-15 02:38:15.910',NULL,NULL,0),(900010,'cadmin3','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','吴敏','13800000010',NULL,1,'2022011010',NULL,NULL,'2026-04-11 20:38:21','2026-03-01 10:00:00.000','2026-04-11 20:38:21.323',NULL,NULL,0),(285095129079808,'test2','$2a$10$Kx9/cOV38StHA4HAajTyr.eGkQC5sjze/LnpyMzwbc/5igu.uF4p6','测试学生2','12345678910','test@test.com',1,'2022011002',NULL,NULL,'2026-04-16 20:38:41','2026-03-16 22:15:02.998','2026-04-16 20:38:40.849',NULL,NULL,0),(285158391963648,'test3','$2a$10$2/.t6IiXz2kW5k52Q3FAU.rU5i/ZPWSytYPOlVx2AdfuSbK1PRDRi','测试学生3',NULL,NULL,1,NULL,NULL,NULL,'2026-04-14 14:16:00','2026-03-17 02:32:28.038','2026-04-14 14:16:00.464',NULL,NULL,0),(285158650445824,'test4','$2a$10$QsDBBwM2.Zk9bBkW66x7Eubih8znpyZ0Cftjgg8nPFcEF1qZtKVKe','测试学生4',NULL,NULL,1,NULL,NULL,NULL,'2026-03-27 14:17:54','2026-03-17 02:33:31.144','2026-03-27 14:17:54.450',NULL,NULL,0),(285159845228544,'test5','$2a$10$3UWRuYX6DeiS3xFbPnn6ruKDleHcTlA8vwkSZk0NvuUnWFvVAUhUe','测试学生5',NULL,NULL,1,NULL,NULL,NULL,'2026-03-26 23:28:09','2026-03-17 02:38:22.839','2026-03-26 23:28:09.175',NULL,NULL,0),(295239277260800,'student99','$2a$10$FQlaEMIgl/XhSaIvQ3L6ceCgazt4s0O/e58hGFxDHJZqaaYfKWyza','student99',NULL,NULL,1,NULL,NULL,NULL,'2026-04-15 02:19:55','2026-04-14 14:11:41.676','2026-04-15 02:19:54.683',NULL,NULL,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `club_id` bigint DEFAULT NULL COMMENT '社团管理员需绑定社团',
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime(3) NOT NULL,
  `updated_at` datetime(3) NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role_club` (`user_id`,`role_id`,`club_id`),
  KEY `idx_role` (`role_id`),
  KEY `idx_club` (`club_id`),
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (3001,2001,1003,NULL,1,'2026-03-11 03:44:32.311','2026-03-11 03:44:32.311',NULL,NULL,0),(910001,900001,1001,NULL,1,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(910002,900002,1001,NULL,1,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(910003,900003,1001,NULL,1,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(910004,900004,1001,NULL,1,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(910005,900005,1001,NULL,1,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(910006,900006,1001,NULL,1,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(910007,900007,1001,NULL,1,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(910008,900008,1001,NULL,1,'2026-03-01 10:00:00.000','2026-03-01 10:00:00.000',NULL,NULL,0),(910009,900009,1002,800001,1,'2026-03-01 10:00:00.000','2026-03-05 10:00:00.000',NULL,NULL,0),(910010,900010,1002,800002,1,'2026-03-01 10:00:00.000','2026-03-05 10:00:00.000',NULL,NULL,0),(283651404611584,2002,1002,NULL,1,'2026-03-12 20:20:31.204','2026-03-13 13:42:02.297',NULL,NULL,0),(283651405934592,2003,1002,286387520438272,1,'2026-03-12 20:20:31.527','2026-03-20 13:53:58.541',NULL,NULL,0),(285095129387008,285095129079808,1001,NULL,1,'2026-03-16 22:15:02.998','2026-03-16 22:15:02.998',NULL,NULL,0),(285158392254464,285158391963648,1001,NULL,1,'2026-03-17 02:32:28.038','2026-03-17 02:32:28.038',NULL,NULL,0),(285158650793984,285158650445824,1001,NULL,1,'2026-03-17 02:33:31.144','2026-03-17 02:33:31.144',NULL,NULL,0),(285159845556224,285159845228544,1001,NULL,1,'2026-03-17 02:38:22.839','2026-03-17 02:38:22.839',NULL,NULL,0),(295239277887488,295239277260800,1002,295239663321088,1,'2026-04-14 14:11:41.676','2026-04-14 14:14:27.672',NULL,NULL,0);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-19 19:03:47
