CREATE DATABASE  IF NOT EXISTS `miyu` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `miyu`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win32 (AMD64)
--
-- Host: 123.207.233.126    Database: miyu
-- ------------------------------------------------------
-- Server version	5.5.21-log

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
-- Table structure for table `friend_ship`
--

DROP TABLE IF EXISTS `friend_ship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `friend_ship` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `friend_open_id` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `deleted` tinyint(4) DEFAULT NULL,
  `friend_nick_name` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `friend_image` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `create_time` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `update_time` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `open_id_index` (`open_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `weixin_user`
--

DROP TABLE IF EXISTS `weixin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `weixin_user` (
  `open_id` varchar(45) CHARACTER SET latin1 NOT NULL,
  `union_id` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `nick_name` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `password` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `country` varchar(20) CHARACTER SET latin1 DEFAULT NULL,
  `province` varchar(20) CHARACTER SET latin1 DEFAULT NULL,
  `city` varchar(20) CHARACTER SET latin1 DEFAULT NULL,
  `avatar_url` varchar(128) CHARACTER SET latin1 DEFAULT NULL,
  `gender` varchar(1) CHARACTER SET latin1 DEFAULT NULL COMMENT '性别 0：未知、1：男、2：女 ',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `deleted` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`open_id`),
  UNIQUE KEY `open_id_UNIQUE` (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'miyu'
--
