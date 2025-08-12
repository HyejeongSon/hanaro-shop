-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: hanarodb
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `batch_job_execution`
--

DROP TABLE IF EXISTS `batch_job_execution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_job_execution` (
  `JOB_EXECUTION_ID` bigint NOT NULL,
  `VERSION` bigint DEFAULT NULL,
  `JOB_INSTANCE_ID` bigint NOT NULL,
  `CREATE_TIME` datetime(6) NOT NULL,
  `START_TIME` datetime(6) DEFAULT NULL,
  `END_TIME` datetime(6) DEFAULT NULL,
  `STATUS` varchar(10) DEFAULT NULL,
  `EXIT_CODE` varchar(2500) DEFAULT NULL,
  `EXIT_MESSAGE` varchar(2500) DEFAULT NULL,
  `LAST_UPDATED` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  KEY `JOB_INST_EXEC_FK` (`JOB_INSTANCE_ID`),
  CONSTRAINT `JOB_INST_EXEC_FK` FOREIGN KEY (`JOB_INSTANCE_ID`) REFERENCES `batch_job_instance` (`JOB_INSTANCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_job_execution`
--

LOCK TABLES `batch_job_execution` WRITE;
/*!40000 ALTER TABLE `batch_job_execution` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_job_execution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_job_execution_context`
--

DROP TABLE IF EXISTS `batch_job_execution_context`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_job_execution_context` (
  `JOB_EXECUTION_ID` bigint NOT NULL,
  `SHORT_CONTEXT` varchar(2500) NOT NULL,
  `SERIALIZED_CONTEXT` text,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_CTX_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `batch_job_execution` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_job_execution_context`
--

LOCK TABLES `batch_job_execution_context` WRITE;
/*!40000 ALTER TABLE `batch_job_execution_context` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_job_execution_context` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_job_execution_params`
--

DROP TABLE IF EXISTS `batch_job_execution_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_job_execution_params` (
  `JOB_EXECUTION_ID` bigint NOT NULL,
  `PARAMETER_NAME` varchar(100) NOT NULL,
  `PARAMETER_TYPE` varchar(100) NOT NULL,
  `PARAMETER_VALUE` varchar(2500) DEFAULT NULL,
  `IDENTIFYING` char(1) NOT NULL,
  KEY `JOB_EXEC_PARAMS_FK` (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_PARAMS_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `batch_job_execution` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_job_execution_params`
--

LOCK TABLES `batch_job_execution_params` WRITE;
/*!40000 ALTER TABLE `batch_job_execution_params` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_job_execution_params` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_job_execution_seq`
--

DROP TABLE IF EXISTS `batch_job_execution_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_job_execution_seq` (
  `ID` bigint NOT NULL,
  `UNIQUE_KEY` char(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_job_execution_seq`
--

LOCK TABLES `batch_job_execution_seq` WRITE;
/*!40000 ALTER TABLE `batch_job_execution_seq` DISABLE KEYS */;
INSERT INTO `batch_job_execution_seq` VALUES (0,'0');
/*!40000 ALTER TABLE `batch_job_execution_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_job_instance`
--

DROP TABLE IF EXISTS `batch_job_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_job_instance` (
  `JOB_INSTANCE_ID` bigint NOT NULL,
  `VERSION` bigint DEFAULT NULL,
  `JOB_NAME` varchar(100) NOT NULL,
  `JOB_KEY` varchar(32) NOT NULL,
  PRIMARY KEY (`JOB_INSTANCE_ID`),
  UNIQUE KEY `JOB_INST_UN` (`JOB_NAME`,`JOB_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_job_instance`
--

LOCK TABLES `batch_job_instance` WRITE;
/*!40000 ALTER TABLE `batch_job_instance` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_job_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_job_seq`
--

DROP TABLE IF EXISTS `batch_job_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_job_seq` (
  `ID` bigint NOT NULL,
  `UNIQUE_KEY` char(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_job_seq`
--

LOCK TABLES `batch_job_seq` WRITE;
/*!40000 ALTER TABLE `batch_job_seq` DISABLE KEYS */;
INSERT INTO `batch_job_seq` VALUES (0,'0');
/*!40000 ALTER TABLE `batch_job_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_step_execution`
--

DROP TABLE IF EXISTS `batch_step_execution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_step_execution` (
  `STEP_EXECUTION_ID` bigint NOT NULL,
  `VERSION` bigint NOT NULL,
  `STEP_NAME` varchar(100) NOT NULL,
  `JOB_EXECUTION_ID` bigint NOT NULL,
  `CREATE_TIME` datetime(6) NOT NULL,
  `START_TIME` datetime(6) DEFAULT NULL,
  `END_TIME` datetime(6) DEFAULT NULL,
  `STATUS` varchar(10) DEFAULT NULL,
  `COMMIT_COUNT` bigint DEFAULT NULL,
  `READ_COUNT` bigint DEFAULT NULL,
  `FILTER_COUNT` bigint DEFAULT NULL,
  `WRITE_COUNT` bigint DEFAULT NULL,
  `READ_SKIP_COUNT` bigint DEFAULT NULL,
  `WRITE_SKIP_COUNT` bigint DEFAULT NULL,
  `PROCESS_SKIP_COUNT` bigint DEFAULT NULL,
  `ROLLBACK_COUNT` bigint DEFAULT NULL,
  `EXIT_CODE` varchar(2500) DEFAULT NULL,
  `EXIT_MESSAGE` varchar(2500) DEFAULT NULL,
  `LAST_UPDATED` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  KEY `JOB_EXEC_STEP_FK` (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_STEP_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `batch_job_execution` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_step_execution`
--

LOCK TABLES `batch_step_execution` WRITE;
/*!40000 ALTER TABLE `batch_step_execution` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_step_execution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_step_execution_context`
--

DROP TABLE IF EXISTS `batch_step_execution_context`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_step_execution_context` (
  `STEP_EXECUTION_ID` bigint NOT NULL,
  `SHORT_CONTEXT` varchar(2500) NOT NULL,
  `SERIALIZED_CONTEXT` text,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  CONSTRAINT `STEP_EXEC_CTX_FK` FOREIGN KEY (`STEP_EXECUTION_ID`) REFERENCES `batch_step_execution` (`STEP_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_step_execution_context`
--

LOCK TABLES `batch_step_execution_context` WRITE;
/*!40000 ALTER TABLE `batch_step_execution_context` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_step_execution_context` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_step_execution_seq`
--

DROP TABLE IF EXISTS `batch_step_execution_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batch_step_execution_seq` (
  `ID` bigint NOT NULL,
  `UNIQUE_KEY` char(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_step_execution_seq`
--

LOCK TABLES `batch_step_execution_seq` WRITE;
/*!40000 ALTER TABLE `batch_step_execution_seq` DISABLE KEYS */;
INSERT INTO `batch_step_execution_seq` VALUES (0,'0');
/*!40000 ALTER TABLE `batch_step_execution_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `cart_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpcttvuq4mxppo8sxggjtn5i2c` (`cart_id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`),
  CONSTRAINT `cart_items_chk_1` CHECK ((`quantity` >= 1))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `member_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKj43ag4hc9ceo08tnwsnol207h` (`member_id`),
  CONSTRAINT `FKr82uc2e12g45wtitmrq51wsmy` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (1,'2025-08-11 23:34:16.880827','2025-08-11 23:34:16.880827',2);
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `daily_product_statistics`
--

DROP TABLE IF EXISTS `daily_product_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `daily_product_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_count` bigint NOT NULL,
  `quantity_sold` bigint NOT NULL,
  `revenue` decimal(10,0) NOT NULL,
  `statistics_date` date NOT NULL,
  `daily_sales_statistics_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6hkxrl8m6esmsgh9obkcc169b` (`statistics_date`,`product_id`),
  KEY `FK9cata91hxcekkkfn453d4df8r` (`daily_sales_statistics_id`),
  KEY `FK7hcsqyt7maknxfyau390beyx9` (`product_id`),
  CONSTRAINT `FK7hcsqyt7maknxfyau390beyx9` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK9cata91hxcekkkfn453d4df8r` FOREIGN KEY (`daily_sales_statistics_id`) REFERENCES `daily_sales_statistics` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daily_product_statistics`
--

LOCK TABLES `daily_product_statistics` WRITE;
/*!40000 ALTER TABLE `daily_product_statistics` DISABLE KEYS */;
INSERT INTO `daily_product_statistics` VALUES (1,1,1,1299000,'2025-08-11',1,1),(2,2,2,90000,'2025-08-11',1,2),(3,1,4,47600,'2025-08-11',1,3);
/*!40000 ALTER TABLE `daily_product_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `daily_sales_statistics`
--

DROP TABLE IF EXISTS `daily_sales_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `daily_sales_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `average_order_amount` decimal(10,2) NOT NULL,
  `canceled_amount` decimal(10,0) NOT NULL,
  `canceled_orders` bigint NOT NULL,
  `statistics_date` date NOT NULL,
  `total_orders` bigint NOT NULL,
  `total_products` bigint NOT NULL,
  `total_sales` decimal(10,0) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKhxbo04c0vhl1iy6ue2a7xm8r` (`statistics_date`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daily_sales_statistics`
--

LOCK TABLES `daily_sales_statistics` WRITE;
/*!40000 ALTER TABLE `daily_sales_statistics` DISABLE KEYS */;
INSERT INTO `daily_sales_statistics` VALUES (1,695150.00,45000,1,'2025-08-11',4,7,2780600);
/*!40000 ALTER TABLE `daily_sales_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deliveries`
--

DROP TABLE IF EXISTS `deliveries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deliveries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `address` varchar(255) NOT NULL,
  `delivered_at` datetime(6) DEFAULT NULL,
  `delivery_request` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `recipient_name` varchar(255) DEFAULT NULL,
  `shipped_at` datetime(6) DEFAULT NULL,
  `status` enum('CANCELED','COMPLETED','PENDING','PREPARING','SHIPPING') NOT NULL,
  `tracking_number` varchar(255) DEFAULT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKk36n9p5v7dd96hpgkwybvbogt` (`order_id`),
  CONSTRAINT `FK7isx0rnbgqr1dcofd5putl6jw` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deliveries`
--

LOCK TABLES `deliveries` WRITE;
/*!40000 ALTER TABLE `deliveries` DISABLE KEYS */;
INSERT INTO `deliveries` VALUES (1,'2025-08-11 23:35:21.749087','2025-08-11 23:35:21.749087','서울시 광진구','2025-08-12 00:00:08.582167','','010-1234-5678','son','2025-08-11 23:45:00.205323','COMPLETED','TRKD20A8A8240FF',1),(2,'2025-08-11 23:35:57.447967','2025-08-11 23:37:26.219723','서울시 광진구',NULL,'','010-1234-5678','son',NULL,'CANCELED',NULL,2),(3,'2025-08-11 23:37:02.210841','2025-08-11 23:37:02.210841','서울시 광진구 구의동','2025-08-12 00:00:08.582167','문앞에 높아주세요.','010-1234-5678','son','2025-08-11 23:45:00.205323','COMPLETED','TRKD20A8A8240FF',3),(4,'2025-08-11 23:52:13.892520','2025-08-11 23:52:13.892520','서울시 도봉구','2025-08-12 00:00:08.582167','','010-1234-5678','Sson','2025-08-12 00:00:08.582167','COMPLETED','TRK0B86B573F1F2',4);
/*!40000 ALTER TABLE `deliveries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `members`
--

DROP TABLE IF EXISTS `members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `nickname` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9d30a9u1qpg8eou0otgkwrp5d` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `members`
--

LOCK TABLES `members` WRITE;
/*!40000 ALTER TABLE `members` DISABLE KEYS */;
INSERT INTO `members` VALUES (1,'2025-08-11 23:28:45.518929','2025-08-11 23:28:45.518929','서울시 중구 을지로 하나로빌딩','hanaro','관리자','admin','$2a$10$hTdcx1YpI1KuSXWR345FUu34ubMCCUYye0ugp.w72jptmp6NCiSNS','02-1234-5678','ADMIN'),(2,'2025-08-11 23:29:12.537506','2025-08-11 23:29:12.537506','서울시 성동구 1번지','user1@hanaro.com','사용자1','user1','$2a$10$CMtJmpScrd5GNcDNAsuZReCX7k8b46BZ7267otGDt7kvULuysV30O','010-1234-5671','USER'),(3,'2025-08-11 23:29:12.776704','2025-08-11 23:29:12.776704','서울시 성동구 2번지','user2@hanaro.com','사용자2','user2','$2a$10$ohJpi3dqMxXNh5nOMSDS3OQ75GYZIpiFDfPnj5O5jCMFQTgLZ0xuW','010-1234-5672','USER'),(4,'2025-08-11 23:29:12.852234','2025-08-11 23:29:12.852234','서울시 성동구 3번지','user3@hanaro.com','사용자3','user3','$2a$10$J5FGMi0W1k0m9.AOhBYXI.3V9YKW48WS1Yh0CAd/oaYl2L5Bpmgqa','010-1234-5673','USER'),(5,'2025-08-11 23:29:12.933386','2025-08-11 23:29:12.933386','서울시 성동구 4번지','user4@hanaro.com','사용자4','user4','$2a$10$HryH3Eb7AgpMrEhs7CypCuFEGIbrpyn/LUUscoZ6wSJyw.qQff55S','010-1234-5674','USER'),(6,'2025-08-11 23:29:13.006324','2025-08-11 23:29:13.006324','서울시 성동구 5번지','user5@hanaro.com','사용자5','user5','$2a$10$G8P4rUzyMBzEXMXmP1vGD.qqRi3HHXQet0xKdx3P1FmO07/2mq6x6','010-1234-5675','USER'),(7,'2025-08-11 23:29:13.080843','2025-08-11 23:29:13.080843','서울시 성동구 6번지','user6@hanaro.com','사용자6','user6','$2a$10$h/tTLI6aWJrGMV/z7tCs7eJM.qPdBJixgQU6QUQOvphnsVxWEMkza','010-1234-5676','USER'),(8,'2025-08-11 23:29:13.157709','2025-08-11 23:29:13.157709','서울시 성동구 7번지','user7@hanaro.com','사용자7','user7','$2a$10$uW2QpKNElQSnAXJHdKiyKe5p4JPCls9iUJRfTK0nT4c4/ArjyGObe','010-1234-5677','USER'),(9,'2025-08-11 23:29:13.236221','2025-08-11 23:29:13.236221','서울시 성동구 8번지','user8@hanaro.com','사용자8','user8','$2a$10$XVnbwLT7LPE2RWIu85ip7.tkjRO.RTmkcEa.F3JizIxTjWtllA72u','010-1234-5678','USER'),(10,'2025-08-11 23:29:13.322225','2025-08-11 23:29:13.322225','서울시 성동구 9번지','user9@hanaro.com','사용자9','user9','$2a$10$kxdlrikuMtnP8IF5IOsSu.LL9CYj8mRWJFH4Rzd7MgGTbHVhxQOky','010-1234-5679','USER'),(11,'2025-08-11 23:29:13.404352','2025-08-11 23:29:13.404352','서울시 성동구 10번지','user10@hanaro.com','사용자10','user10','$2a$10$rUSEToC0KoAL7kOullcXkuKp2FW2yhWzvQrvUb/dIYOB5l1mvEJpm','010-1234-5670','USER'),(12,'2025-08-11 23:29:13.485882','2025-08-11 23:29:13.485882','서울시 성동구 11번지','user11@hanaro.com','사용자11','user11','$2a$10$4rTuwZzWzfSkqniEPtoF0OMRRI/Pr5ugE5mIr9r6Bwtd5E4r5WlDS','010-1234-5671','USER'),(13,'2025-08-11 23:29:13.568401','2025-08-11 23:29:13.568401','서울시 성동구 12번지','user12@hanaro.com','사용자12','user12','$2a$10$DaTqTp72mm4LiUO5EXI4neh4ZJRkRu8AXSomNcrXuUFM545BZqx66','010-1234-5672','USER'),(14,'2025-08-11 23:29:13.652905','2025-08-11 23:29:13.652905','서울시 성동구 13번지','user13@hanaro.com','사용자13','user13','$2a$10$hB.SdEN2nhfS0J1TRN32QuaR2rpn14EVx/xkRVKwCRGqlao3bV1qm','010-1234-5673','USER'),(15,'2025-08-11 23:29:13.740638','2025-08-11 23:29:13.740638','서울시 성동구 14번지','user14@hanaro.com','사용자14','user14','$2a$10$e/nEa4b2GWIhpH7qvmMeqeTtsYEeCJK3any55tyZwnIu1oJLMEw9W','010-1234-5674','USER'),(16,'2025-08-11 23:29:13.831989','2025-08-11 23:29:13.831989','서울시 성동구 15번지','user15@hanaro.com','사용자15','user15','$2a$10$t8Z1eL08x8Ehm5ZEEU4qQukGa.0jtgCS4L2imT9fokZZ//cMJwDjS','010-1234-5675','USER');
/*!40000 ALTER TABLE `members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `product_name` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `order_items_chk_1` CHECK ((`quantity` >= 1))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,'2025-08-11 23:35:21.763287','2025-08-11 23:35:21.763287','갤럭시 S25',1,1299000.00,1299000.00,1,1),(2,'2025-08-11 23:35:21.783209','2025-08-11 23:35:21.783209','이펙티브 자바 3/E',1,45000.00,45000.00,1,2),(3,'2025-08-11 23:35:57.450965','2025-08-11 23:35:57.450965','이펙티브 자바 3/E',1,45000.00,45000.00,2,2),(4,'2025-08-11 23:37:02.212841','2025-08-11 23:37:02.212841','이펙티브 자바 3/E',1,45000.00,45000.00,3,2),(5,'2025-08-11 23:52:13.950617','2025-08-11 23:52:13.950617','고구마',4,47600.00,11900.00,4,3);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `delivery_address` varchar(255) NOT NULL,
  `delivery_phone` varchar(255) DEFAULT NULL,
  `delivery_request` varchar(255) DEFAULT NULL,
  `order_number` varchar(255) NOT NULL,
  `status` enum('CANCELED','ORDERED') NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `member_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnthkiu7pgmnqnu86i2jyoe2v7` (`order_number`),
  KEY `FK2vq7lo4gkknrmghj3rqpqqg6s` (`member_id`),
  CONSTRAINT `FK2vq7lo4gkknrmghj3rqpqqg6s` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2025-08-11 23:35:21.730362','2025-08-11 23:35:21.730362','서울시 광진구','010-1234-5678','','ORD202508112B9329E7','ORDERED',1344000.00,2),(2,'2025-08-11 23:35:57.444358','2025-08-11 23:37:26.219723','서울시 광진구','010-1234-5678','','ORD20250811693E4B67','CANCELED',45000.00,2),(3,'2025-08-11 23:37:02.206840','2025-08-11 23:37:02.206840','서울시 광진구 구의동','010-1234-5678','문앞에 높아주세요.','ORD20250811D449BFAF','ORDERED',45000.00,2),(4,'2025-08-11 23:52:13.803148','2025-08-11 23:52:13.803148','서울시 도봉구','010-1234-5678','','ORD20250811B488A88C','ORDERED',47600.00,2);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_images`
--

DROP TABLE IF EXISTS `product_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `file_size` bigint DEFAULT NULL,
  `is_main_image` bit(1) DEFAULT NULL,
  `is_thumbnail` bit(1) DEFAULT NULL,
  `original_file_name` varchar(255) NOT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqnq71xsohugpqwf3c9gxmsuy` (`product_id`),
  CONSTRAINT `FKqnq71xsohugpqwf3c9gxmsuy` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_images`
--

LOCK TABLES `product_images` WRITE;
/*!40000 ALTER TABLE `product_images` DISABLE KEYS */;
INSERT INTO `product_images` VALUES (1,'2025-08-11 23:30:15.446214','2025-08-11 23:30:15.446214','6b8f9bf5-e8b0-4e34-ae3d-df496b75567c_갤럭시s25.png','/2025/08/11/6b8f9bf5-e8b0-4e34-ae3d-df496b75567c_갤럭시s25.png',160940,_binary '',_binary '\0','갤럭시s25.png','6b8f9bf5-e8b0-4e34-ae3d-df496b75567c',1),(2,'2025-08-11 23:30:15.473596','2025-08-11 23:30:15.473596','s_6b8f9bf5-e8b0-4e34-ae3d-df496b75567c_갤럭시s25.png','/2025/08/11/s_6b8f9bf5-e8b0-4e34-ae3d-df496b75567c_갤럭시s25.png',39315,_binary '\0',_binary '','s_갤럭시s25.png','6b8f9bf5-e8b0-4e34-ae3d-df496b75567c',1),(3,'2025-08-11 23:30:40.824655','2025-08-11 23:30:40.824655','a0f0c821-66e6-485c-802c-54e3c339f540_effective_java_front.jpg','/2025/08/11/a0f0c821-66e6-485c-802c-54e3c339f540_effective_java_front.jpg',133022,_binary '',_binary '\0','effective_java_front.jpg','a0f0c821-66e6-485c-802c-54e3c339f540',2),(4,'2025-08-11 23:30:40.831694','2025-08-11 23:30:40.831694','s_a0f0c821-66e6-485c-802c-54e3c339f540_effective_java_front.jpg','/2025/08/11/s_a0f0c821-66e6-485c-802c-54e3c339f540_effective_java_front.jpg',5711,_binary '\0',_binary '','s_effective_java_front.jpg','a0f0c821-66e6-485c-802c-54e3c339f540',2),(5,'2025-08-11 23:30:40.834708','2025-08-11 23:30:40.834708','a0f0c821-66e6-485c-802c-54e3c339f540_effective_java_back.png','/2025/08/11/a0f0c821-66e6-485c-802c-54e3c339f540_effective_java_back.png',199664,_binary '\0',_binary '\0','effective_java_back.png','a0f0c821-66e6-485c-802c-54e3c339f540',2),(6,'2025-08-11 23:30:40.836710','2025-08-11 23:30:40.836710','s_a0f0c821-66e6-485c-802c-54e3c339f540_effective_java_back.png','/2025/08/11/s_a0f0c821-66e6-485c-802c-54e3c339f540_effective_java_back.png',33015,_binary '\0',_binary '','s_effective_java_back.png','a0f0c821-66e6-485c-802c-54e3c339f540',2),(7,'2025-08-11 23:48:27.217154','2025-08-11 23:48:27.217154','db123a41-9a40-441c-9a51-5ac9c87d4e6c_고구마.png','/2025/08/11/db123a41-9a40-441c-9a51-5ac9c87d4e6c_고구마.png',74900,_binary '',_binary '\0','고구마.png','db123a41-9a40-441c-9a51-5ac9c87d4e6c',3),(8,'2025-08-11 23:48:27.333474','2025-08-11 23:48:27.333474','s_db123a41-9a40-441c-9a51-5ac9c87d4e6c_고구마.png','/2025/08/11/s_db123a41-9a40-441c-9a51-5ac9c87d4e6c_고구마.png',37274,_binary '\0',_binary '','s_고구마.png','db123a41-9a40-441c-9a51-5ac9c87d4e6c',3);
/*!40000 ALTER TABLE `product_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category` enum('BEAUTY','BOOK','CLOTHING','ELECTRONICS','FOOD') NOT NULL,
  `description` text NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock_quantity` int NOT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `products_chk_1` CHECK ((`price` >= 0)),
  CONSTRAINT `products_chk_2` CHECK ((`stock_quantity` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'2025-08-11 23:30:14.984518','2025-08-11 23:35:21.834395','ELECTRONICS','삼성 갤럭시 S25 최신 스마트폰입니다. 강력한 성능과 뛰어난 카메라 기능을 제공합니다.',_binary '','갤럭시 S25',1299000.00,99,_binary '\0'),(2,'2025-08-11 23:30:40.422124','2025-08-11 23:40:25.444151','BOOK','Joshua Bloch 저, 자바 개발자 필독서. 고급 자바 프로그래밍 기법과 모범 사례를 담았습니다.',_binary '','이펙티브 자바 3/E',45000.00,60,_binary '\0'),(3,'2025-08-11 23:48:26.216992','2025-08-12 00:08:38.197621','FOOD','전남 고흥 햇고구마로, 달콤하고 촉촉한 맛이 일품인 고구마입니다.',_binary '','고구마',11900.00,150,_binary '\0');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_tokens`
--

DROP TABLE IF EXISTS `refresh_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` text NOT NULL,
  `member_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6vmhlugnhc5iqbcdv1i5ebswn` (`member_id`),
  CONSTRAINT `FK9bb9t1ma4ltt5ngdk10bkna2c` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_tokens`
--

LOCK TABLES `refresh_tokens` WRITE;
/*!40000 ALTER TABLE `refresh_tokens` DISABLE KEYS */;
INSERT INTO `refresh_tokens` VALUES (1,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzU0OTIzNzQ2LCJleHAiOjE3NTU1Mjg1NDZ9.veOGMgqNpL4_1sEchZnfoWEpIU-cJdc4zUU3v-1Fnyk',2),(2,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzU0OTk5NjEwLCJleHAiOjE3NTU2MDQ0MTB9.GdTqdvSlM5Sa6GcMuWpyOllZ-MX1ZkeO9Cvu7vRzO_w',1);
/*!40000 ALTER TABLE `refresh_tokens` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-12 21:06:59
