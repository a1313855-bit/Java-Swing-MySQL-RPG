CREATE DATABASE  IF NOT EXISTS `homework3.0` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `homework3.0`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: homework3.0
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `hero`
--

DROP TABLE IF EXISTS `hero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hero` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色唯一的識別 ID，主鍵',
  `player_id` int NOT NULL COMMENT '所屬的玩家帳號 ID，對應 player 表的 id 欄位',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名稱，不可重複',
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '性別 (男性/女性)',
  `img_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '使用的像素頭像圖片檔名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `player_id` (`player_id`),
  CONSTRAINT `hero_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hero`
--

LOCK TABLES `hero` WRITE;
/*!40000 ALTER TABLE `hero` DISABLE KEYS */;
INSERT INTO `hero` VALUES (1,1,'亞瑟','男性','male_hero.jpg'),(2,1,'愛麗絲','女性','female_hero.jpg');
/*!40000 ALTER TABLE `hero` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hero_stats`
--

DROP TABLE IF EXISTS `hero_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hero_stats` (
  `hero_id` int NOT NULL COMMENT '所屬角色的 ID，主鍵且為外鍵',
  `level` int DEFAULT '1' COMMENT '等級',
  `exp` int DEFAULT '0' COMMENT '經驗值',
  `hp` int DEFAULT '100' COMMENT '生命值',
  `atk` int DEFAULT '10' COMMENT '攻擊力',
  `def` int DEFAULT '5' COMMENT '防禦力',
  `speed` int DEFAULT '10' COMMENT '速度',
  `stat_points` int DEFAULT '0' COMMENT '未分配屬性點',
  PRIMARY KEY (`hero_id`),
  CONSTRAINT `hero_stats_ibfk_1` FOREIGN KEY (`hero_id`) REFERENCES `hero` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hero_stats`
--

LOCK TABLES `hero_stats` WRITE;
/*!40000 ALTER TABLE `hero_stats` DISABLE KEYS */;
INSERT INTO `hero_stats` VALUES (1,5,250,100,25,12,15,0),(2,3,120,100,30,8,20,0);
/*!40000 ALTER TABLE `hero_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '玩家帳號唯一的識別 ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '玩家登入帳號，不可重複',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '玩家登入密碼',
  `nick_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '玩家顯示的暱稱',
  `mail` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '玩家的電子信箱',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '帳號註冊/創建時間',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
INSERT INTO `player` VALUES (1,'testuser','123456','測試小勇者','test@game.com','2026-07-03 06:26:26');
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monster`
--

DROP TABLE IF EXISTS `monster`;
CREATE TABLE `monster` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '怪物的唯一 ID',
  `name` varchar(50) NOT NULL COMMENT '怪物名稱',
  `map_id` int NOT NULL COMMENT '所屬地圖 ID (1:新手草原, 2:幽暗森林, 3:烈焰火山)',
  `level` int DEFAULT '1' COMMENT '怪物推薦等級',
  `hp` int NOT NULL DEFAULT '30' COMMENT '怪物生命值上限',
  `atk` int NOT NULL DEFAULT '5' COMMENT '怪物攻擊力',
  `def` int NOT NULL DEFAULT '2' COMMENT '怪物防禦力',
  `speed` int NOT NULL DEFAULT '5' COMMENT '怪物速度',
  `exp_reward` int NOT NULL DEFAULT '10' COMMENT '擊敗後的經驗值',
  `drop_rate` double DEFAULT '0.05' COMMENT '裝備掉落率 (如 0.05 代表 5% 掉落率)',
  `loot_tier` int DEFAULT '1' COMMENT '掉落裝備階級 (1: 基礎, 2: 中級, 3: 高級)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `monster`
--

LOCK TABLES `monster` WRITE;
INSERT INTO `monster` VALUES 
(1,'綠史萊姆',1,1,30,4,1,4,8,0.05,1),
(2,'幼草蛇',1,2,40,6,2,6,12,0.06,1),
(3,'小野狼',1,4,60,9,3,8,20,0.08,1),
(4,'哥布林',2,6,100,14,5,10,35,0.10,2),
(5,'毒蜘蛛',2,8,130,18,7,12,50,0.12,2),
(6,'食人花',2,10,180,24,10,8,70,0.15,2),
(7,'熔岩魔',3,11,250,32,14,10,100,0.18,3),
(8,'火蜥蜴',3,13,320,40,18,14,140,0.20,3),
(9,'烈焰巨龍 (BOSS)',3,15,500,55,25,12,250,0.35,3);
UNLOCK TABLES;

--
-- Table structure for table `equipment`
--

DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '裝備名稱',
  `slot` varchar(20) NOT NULL COMMENT '裝備位置 (helmet, armor, weapon, boots)',
  `tier` int DEFAULT '1' COMMENT '裝備品級 (1, 2, 3)',
  `hp_bonus` int DEFAULT '0' COMMENT '生命值加成',
  `atk_bonus` int DEFAULT '0' COMMENT '攻擊力加成',
  `def_bonus` int DEFAULT '0' COMMENT '防禦力加成',
  `speed_bonus` int DEFAULT '0' COMMENT '速度加成',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `equipment`
--

LOCK TABLES `equipment` WRITE;
INSERT INTO `equipment` VALUES 
(1,'新手木劍','weapon',1,0,2,0,0),
(2,'皮革帽','helmet',1,15,0,0,0),
(3,'新手布衣','armor',1,0,0,1,0),
(4,'草鞋','boots',1,0,0,0,1),
(5,'精鋼大劍','weapon',2,0,5,0,0),
(6,'騎士頭盔','helmet',2,35,0,0,0),
(7,'鎖子甲','armor',2,0,0,3,0),
(8,'皮靴','boots',2,0,0,0,2),
(9,'熔岩神劍','weapon',3,0,12,0,0),
(10,'熔岩頭盔','helmet',3,80,0,0,0),
(11,'熔岩胸甲','armor',3,0,0,8,0),
(12,'重裝戰靴','boots',3,0,0,0,4);
UNLOCK TABLES;

--
-- Table structure for table `hero_equipment`
--

DROP TABLE IF EXISTS `hero_equipment`;
CREATE TABLE `hero_equipment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hero_id` int NOT NULL COMMENT '擁有者角色 ID',
  `equipment_id` int NOT NULL COMMENT '裝備範本 ID',
  `is_equipped` tinyint DEFAULT '0' COMMENT '是否穿戴中 (1:穿戴, 0:背包中)',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_hero_equip_hero` FOREIGN KEY (`hero_id`) REFERENCES `hero` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_hero_equip_item` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Temporary view structure for view `v_hero_detail`
--

DROP TABLE IF EXISTS `v_hero_detail`;
/*!50001 DROP VIEW IF EXISTS `v_hero_detail`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_hero_detail` AS SELECT 
 1 AS `id`,
 1 AS `player_id`,
 1 AS `name`,
 1 AS `gender`,
 1 AS `img_name`,
 1 AS `level`,
 1 AS `exp`,
 1 AS `hp`,
 1 AS `atk`,
 1 AS `def`,
 1 AS `speed`,
 1 AS `stat_points`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_hero_detail`
--

/*!50001 DROP VIEW IF EXISTS `v_hero_detail`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_hero_detail` AS select `h`.`id` AS `id`,`h`.`player_id` AS `player_id`,`h`.`name` AS `name`,`h`.`gender` AS `gender`,`h`.`img_name` AS `img_name`,`s`.`level` AS `level`,`s`.`exp` AS `exp`,`s`.`hp` AS `hp`,`s`.`atk` AS `atk`,`s`.`def` AS `def`,`s`.`speed` AS `speed`,`s`.`stat_points` AS `stat_points` from (`hero` `h` join `hero_stats` `s` on((`h`.`id` = `s`.`hero_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Structure for view `v_hero_equipment`
--

DROP TABLE IF EXISTS `v_hero_equipment`;
DROP VIEW IF EXISTS `v_hero_equipment`;
CREATE VIEW `v_hero_equipment` AS select `he`.`id` AS `id`,`he`.`hero_id` AS `hero_id`,`he`.`equipment_id` AS `equipment_id`,`he`.`is_equipped` AS `is_equipped`,`e`.`name` AS `name`,`e`.`slot` AS `slot`,`e`.`tier` AS `tier`,`e`.`hp_bonus` AS `hp_bonus`,`e`.`atk_bonus` AS `atk_bonus`,`e`.`def_bonus` AS `def_bonus`,`e`.`speed_bonus` AS `speed_bonus` from (`hero_equipment` `he` join `equipment` `e` on((`he`.`equipment_id` = `e`.`id`)));

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-03 16:31:50
