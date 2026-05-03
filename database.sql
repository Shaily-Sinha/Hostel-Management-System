CREATE DATABASE  IF NOT EXISTS `hostel_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hostel_db`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: hostel_db
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `resident_id` int NOT NULL,
  `attendance_date` date NOT NULL,
  `check_in_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `location_lat` decimal(10,8) DEFAULT NULL,
  `location_lng` decimal(11,8) DEFAULT NULL,
  `biometric_verified` tinyint(1) DEFAULT '0',
  `location_verified` tinyint(1) DEFAULT '0',
  `status` varchar(20) DEFAULT 'PRESENT',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_attendance` (`resident_id`,`attendance_date`),
  CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`resident_id`) REFERENCES `residents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (1,1,'2026-04-27','2026-04-27 10:26:26',30.41595319,77.96912315,0,0,'ABSENT');
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `resident_id` int NOT NULL,
  `room_id` int NOT NULL,
  `booking_date` date NOT NULL,
  `check_in_date` date NOT NULL,
  `check_out_date` date DEFAULT NULL,
  `status` varchar(20) DEFAULT 'PENDING',
  `total_amount` decimal(10,2) DEFAULT NULL,
  `notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `resident_id` (`resident_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`resident_id`) REFERENCES `residents` (`id`),
  CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,1,1,'2026-03-20','2026-04-01','2026-06-30','CONFIRMED',27000.00,'Full semester booking','2026-04-25 17:33:21'),(2,2,2,'2026-03-22','2026-04-01','2026-06-30','CONFIRMED',27000.00,'Full semester booking','2026-04-25 17:33:21'),(3,3,3,'2026-03-25','2026-04-02','2026-06-30','CONFIRMED',21000.00,'Full semester booking','2026-04-25 17:33:21'),(4,4,4,'2026-03-28','2026-04-02','2026-06-30','CONFIRMED',24000.00,'Shared room with Krishna','2026-04-25 17:33:21'),(5,5,4,'2026-03-28','2026-04-03','2026-06-30','CONFIRMED',24000.00,'Shared room with Aditya','2026-04-25 17:33:21'),(6,6,5,'2026-03-30','2026-04-03','2026-06-30','CONFIRMED',19500.00,'Shared room with Arjun','2026-04-25 17:33:21'),(7,7,5,'2026-03-30','2026-04-04','2026-06-30','CONFIRMED',19500.00,'Shared room with Rohan','2026-04-25 17:33:21'),(8,8,6,'2026-04-01','2026-04-04','2026-06-30','PENDING',16500.00,'Awaiting confirmation','2026-04-25 17:33:21'),(9,9,7,'2026-04-02','2026-04-05','2026-06-30','PENDING',21000.00,'Awaiting confirmation','2026-04-25 17:33:21'),(10,10,8,'2026-04-03','2026-04-05','2026-06-30','PENDING',28500.00,'Premium single room','2026-04-25 17:33:21');
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_menu`
--

DROP TABLE IF EXISTS `food_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_menu` (
  `id` int NOT NULL AUTO_INCREMENT,
  `menu_date` date NOT NULL,
  `meal_type` varchar(20) NOT NULL,
  `items` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_menu` (`menu_date`,`meal_type`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_menu`
--

LOCK TABLES `food_menu` WRITE;
/*!40000 ALTER TABLE `food_menu` DISABLE KEYS */;
INSERT INTO `food_menu` VALUES (1,'2026-04-25','Breakfast','Poha, Jalebi, Milk, Banana','2026-04-25 17:33:21'),(2,'2026-04-25','Lunch','Dal Tadka, Rice, Chapati, Mix Veg, Raita, Gulab Jamun','2026-04-25 17:33:21'),(3,'2026-04-25','Dinner','Paneer Butter Masala, Naan, Jeera Rice, Salad, Ice Cream','2026-04-25 17:33:21'),(4,'2026-04-26','Breakfast','Aloo Paratha, Curd, Pickle, Tea','2026-04-25 17:33:21'),(5,'2026-04-26','Lunch','Rajma Chawal, Chapati, Aloo Gobi, Papad, Lassi','2026-04-25 17:33:21'),(6,'2026-04-26','Dinner','Chicken Curry, Rice, Chapati, Cucumber Raita, Fruit Custard','2026-04-25 17:33:21'),(7,'2026-04-27','Breakfast','Idli, Sambhar, Coconut Chutney, Filter Coffee','2026-04-25 17:33:21'),(8,'2026-04-27','Lunch','Sambar Rice, Avial, Rasam, Papad, Payasam','2026-04-25 17:33:21'),(9,'2026-04-27','Dinner','Biryani, Raita, Mirchi Ka Salan, Double Ka Meetha','2026-04-25 17:33:21'),(10,'2026-04-28','Breakfast','Upma, Coconut Chutney, Boiled Eggs, Tea','2026-04-25 17:33:21'),(11,'2026-04-28','Lunch','Chole Bhature, Pulao, Raita, Salad, Kesari Halwa','2026-04-25 17:33:21'),(12,'2026-04-28','Dinner','Matar Paneer, Tandoori Roti, Veg Pulao, Dal Makhani, Gajar Ka Halwa','2026-04-25 17:33:21');
/*!40000 ALTER TABLE `food_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `late_arrival_intimations`
--

DROP TABLE IF EXISTS `late_arrival_intimations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `late_arrival_intimations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `resident_id` int NOT NULL,
  `arrival_date` date NOT NULL,
  `expected_time` varchar(10) NOT NULL,
  `reason` text NOT NULL,
  `status` varchar(20) DEFAULT 'NOTIFIED',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `resident_id` (`resident_id`),
  CONSTRAINT `late_arrival_intimations_ibfk_1` FOREIGN KEY (`resident_id`) REFERENCES `residents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `late_arrival_intimations`
--

LOCK TABLES `late_arrival_intimations` WRITE;
/*!40000 ALTER TABLE `late_arrival_intimations` DISABLE KEYS */;
INSERT INTO `late_arrival_intimations` VALUES (1,1,'2026-04-25','22:30','Late library study session','ACKNOWLEDGED','2026-04-25 17:33:21'),(2,2,'2026-04-24','23:00','Movie night with friends','NOTIFIED','2026-04-25 17:33:21'),(3,3,'2026-04-23','21:45','College project group meeting','EXCUSED','2026-04-25 17:33:21'),(4,4,'2026-04-22','22:00','Traffic jam due to rain','ACKNOWLEDGED','2026-04-25 17:33:21'),(5,5,'2026-04-25','23:30','Part-time job shift extended','NOTIFIED','2026-04-25 17:33:21'),(6,6,'2026-04-21','20:15','Sports practice ran late','EXCUSED','2026-04-25 17:33:21'),(7,7,'2026-04-24','22:45','Family dinner outside','ACKNOWLEDGED','2026-04-25 17:33:21'),(8,8,'2026-04-23','21:00','Missed college bus','NOTIFIED','2026-04-25 17:33:21'),(9,9,'2026-04-22','23:15','Hackathon participation','EXCUSED','2026-04-25 17:33:21'),(10,10,'2026-04-25','22:30','Shopping for hostel supplies','ACKNOWLEDGED','2026-04-25 17:33:21');
/*!40000 ALTER TABLE `late_arrival_intimations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leave_requests`
--

DROP TABLE IF EXISTS `leave_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_requests` (
  `id` int NOT NULL AUTO_INCREMENT,
  `resident_id` int NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `reason` text NOT NULL,
  `status` varchar(20) DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `resident_id` (`resident_id`),
  CONSTRAINT `leave_requests_ibfk_1` FOREIGN KEY (`resident_id`) REFERENCES `residents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_requests`
--

LOCK TABLES `leave_requests` WRITE;
/*!40000 ALTER TABLE `leave_requests` DISABLE KEYS */;
INSERT INTO `leave_requests` VALUES (1,1,'2026-04-20','2026-04-22','Family function at home','APPROVED','2026-04-25 17:33:21'),(2,2,'2026-04-21','2026-04-23','Medical appointment','PENDING','2026-04-25 17:33:21'),(3,3,'2026-04-24','2026-04-26','Sister wedding','APPROVED','2026-04-25 17:33:21'),(4,4,'2026-04-18','2026-04-19','Personal work','REJECTED','2026-04-25 17:33:21'),(5,5,'2026-04-27','2026-04-30','Exam preparation at home','PENDING','2026-04-25 17:33:21'),(6,6,'2026-04-22','2026-04-24','Festival celebration','APPROVED','2026-04-25 17:33:21'),(7,7,'2026-04-25','2026-04-25','One day outing','PENDING','2026-04-25 17:33:21'),(8,8,'2026-04-19','2026-04-20','Relative visit','REJECTED','2026-04-25 17:33:21'),(9,9,'2026-04-28','2026-05-02','Summer vacation early leave','PENDING','2026-04-25 17:33:21'),(10,10,'2026-04-23','2026-04-24','College fest participation','APPROVED','2026-04-25 17:33:21');
/*!40000 ALTER TABLE `leave_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mess_bills`
--

DROP TABLE IF EXISTS `mess_bills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mess_bills` (
  `id` int NOT NULL AUTO_INCREMENT,
  `resident_id` int NOT NULL,
  `bill_month` varchar(7) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `paid_amount` decimal(10,2) DEFAULT '0.00',
  `due_amount` decimal(10,2) NOT NULL,
  `status` varchar(20) DEFAULT 'UNPAID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_bill` (`resident_id`,`bill_month`),
  CONSTRAINT `mess_bills_ibfk_1` FOREIGN KEY (`resident_id`) REFERENCES `residents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mess_bills`
--

LOCK TABLES `mess_bills` WRITE;
/*!40000 ALTER TABLE `mess_bills` DISABLE KEYS */;
INSERT INTO `mess_bills` VALUES (1,1,'2026-04',4500.00,4500.00,0.00,'PAID','2026-04-25 17:33:21'),(2,2,'2026-04',4500.00,2000.00,2500.00,'PARTIAL','2026-04-25 17:33:21'),(3,3,'2026-04',4500.00,0.00,4500.00,'UNPAID','2026-04-25 17:33:21'),(4,4,'2026-04',4500.00,4500.00,0.00,'PAID','2026-04-25 17:33:21'),(5,5,'2026-04',4500.00,1500.00,3000.00,'PARTIAL','2026-04-25 17:33:21'),(6,6,'2026-04',4500.00,0.00,4500.00,'UNPAID','2026-04-25 17:33:21'),(7,7,'2026-04',4500.00,4500.00,0.00,'PAID','2026-04-25 17:33:21'),(8,8,'2026-04',4500.00,3000.00,1500.00,'PARTIAL','2026-04-25 17:33:21'),(9,9,'2026-04',4500.00,0.00,4500.00,'UNPAID','2026-04-25 17:33:21'),(10,10,'2026-04',4500.00,4500.00,0.00,'PAID','2026-04-25 17:33:21');
/*!40000 ALTER TABLE `mess_bills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `resident_id` int DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `message` text NOT NULL,
  `type` varchar(20) DEFAULT 'BROADCAST',
  `status` varchar(20) DEFAULT 'UNREAD',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sender_id` (`sender_id`),
  KEY `resident_id` (`resident_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`resident_id`) REFERENCES `residents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,1,NULL,'Welcome to Hostel','Welcome to the new semester! Please collect your ID cards from the office.','BROADCAST','UNREAD','2026-04-25 17:33:21'),(2,1,NULL,'Mess Menu Update','New South Indian breakfast items have been added to the mess menu starting Monday.','BROADCAST','READ','2026-04-25 17:33:21'),(3,1,NULL,'Maintenance Notice','Water supply will be interrupted on Sunday from 10 AM to 2 PM for tank cleaning.','BROADCAST','READ','2026-04-25 17:33:21'),(4,1,1,'Leave Request Approved','Your leave request from 20-Apr to 22-Apr has been approved. Have a safe journey!','PERSONAL','UNREAD','2026-04-25 17:33:21'),(5,1,2,'Mess Bill Pending','Your mess bill for April 2026 is partially paid. Please clear the remaining 2500.','PERSONAL','UNREAD','2026-04-25 17:33:21'),(6,1,3,'Late Arrival Excused','Your late arrival intimation for 23-Apr has been excused by the warden.','PERSONAL','READ','2026-04-25 17:33:21'),(7,1,4,'Room Allocation Notice','You have been allocated to Room 104 (Double Sharing). Welcome!','PERSONAL','READ','2026-04-25 17:33:21'),(8,1,5,'Leave Request Received','Your leave request is under review. You will be notified once it is processed.','PERSONAL','UNREAD','2026-04-25 17:33:21'),(9,1,6,'Student ID Expired','Your hostel ID card has expired on 31-Mar-2025. Please apply for renewal immediately.','PERSONAL','UNREAD','2026-04-25 17:33:21'),(10,1,8,'Late Arrival Rejected','Your late arrival request for 19-Apr has been rejected. Please adhere to hostel timings.','PERSONAL','READ','2026-04-25 17:33:21');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `residents`
--

DROP TABLE IF EXISTS `residents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `residents` (
  `id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text,
  `emergency_contact` varchar(100) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `id_proof` varchar(100) DEFAULT NULL,
  `room_id` int DEFAULT NULL,
  `check_in_date` date DEFAULT NULL,
  `check_out_date` date DEFAULT NULL,
  `status` varchar(20) DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `residents_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `residents`
--

LOCK TABLES `residents` WRITE;
/*!40000 ALTER TABLE `residents` DISABLE KEYS */;
INSERT INTO `residents` VALUES (1,'Aarav Sharma','aarav@email.com','9876543210','Delhi','Rajesh Sharma','2002-03-15','Male','Aadhaar-1234',1,'2026-04-01',NULL,'ACTIVE','2026-04-25 17:33:21'),(2,'Vihaan Patel','vihaan@email.com','9876543211','Mumbai','Anita Patel','2001-07-22','Male','Aadhaar-1235',2,'2026-04-01',NULL,'ACTIVE','2026-04-25 17:33:21'),(3,'Ishaan Gupta','ishaan@email.com','9876543212','Bangalore','Suresh Gupta','2003-01-10','Male','Aadhaar-1236',3,'2026-04-02',NULL,'ACTIVE','2026-04-25 17:33:21'),(4,'Aditya Singh','aditya@email.com','9876543213','Jaipur','Meera Singh','2002-11-05','Male','Aadhaar-1237',4,'2026-04-02',NULL,'ACTIVE','2026-04-25 17:33:21'),(5,'Krishna Rao','krishna@email.com','9876543214','Hyderabad','Lakshmi Rao','2001-09-18','Male','Aadhaar-1238',4,'2026-04-03',NULL,'ACTIVE','2026-04-25 17:33:21'),(6,'Rohan Mehta','rohan@email.com','9876543215','Pune','Priya Mehta','2002-05-30','Male','Aadhaar-1239',5,'2026-04-03',NULL,'ACTIVE','2026-04-25 17:33:21'),(7,'Arjun Nair','arjun@email.com','9876543216','Chennai','Vijay Nair','2003-02-14','Male','Aadhaar-1240',5,'2026-04-04',NULL,'ACTIVE','2026-04-25 17:33:21'),(8,'Dhruv Kumar','dhruv@email.com','9876543217','Kolkata','Sunita Kumar','2002-08-08','Male','Aadhaar-1241',6,'2026-04-04',NULL,'ACTIVE','2026-04-25 17:33:21'),(9,'Vivaan Reddy','vivaan@email.com','9876543218','Visakhapatnam','Ravi Reddy','2001-12-25','Male','Aadhaar-1242',7,'2026-04-05',NULL,'ACTIVE','2026-04-25 17:33:21'),(10,'Ayaan Khan','ayaan@email.com','9876543219','Lucknow','Fatima Khan','2003-04-02','Male','Aadhaar-1243',8,'2026-04-05',NULL,'ACTIVE','2026-04-25 17:33:21');
/*!40000 ALTER TABLE `residents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_number` varchar(20) NOT NULL,
  `room_type` varchar(50) NOT NULL,
  `capacity` int NOT NULL,
  `price_per_month` decimal(10,2) NOT NULL,
  `status` varchar(20) DEFAULT 'AVAILABLE',
  `floor` int DEFAULT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `room_number` (`room_number`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (1,'101','Single',1,9000.00,'AVAILABLE',1,'AC, WiFi, Personal Washroom, Study Table, Wardrobe','2026-04-25 17:24:38'),(2,'102','Single',1,9000.00,'AVAILABLE',1,'AC, WiFi, Personal Washroom, Study Table, Wardrobe','2026-04-25 17:24:38'),(3,'103','Single',1,7000.00,'AVAILABLE',1,'Non-AC, WiFi, Shared Washroom, Study Table, Wardrobe','2026-04-25 17:24:38'),(4,'104','Double',2,8000.00,'AVAILABLE',1,'AC, WiFi, Personal Washroom, 2 Study Tables, 2 Wardrobes','2026-04-25 17:24:38'),(5,'105','Double',2,6500.00,'AVAILABLE',1,'Non-AC, WiFi, Shared Washroom, 2 Study Tables, 2 Wardrobes','2026-04-25 17:24:38'),(6,'106','Triple',3,5500.00,'AVAILABLE',1,'Non-AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes','2026-04-25 17:24:38'),(7,'107','Triple',3,7000.00,'AVAILABLE',1,'AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes','2026-04-25 17:24:38'),(8,'108','Single',1,9500.00,'AVAILABLE',1,'Premium AC, WiFi, Personal Washroom, TV, Mini Fridge','2026-04-25 17:24:38'),(9,'201','Single',1,9000.00,'AVAILABLE',2,'AC, WiFi, Personal Washroom, Study Table, Wardrobe','2026-04-25 17:24:38'),(10,'202','Single',1,7000.00,'AVAILABLE',2,'Non-AC, WiFi, Shared Washroom, Study Table, Wardrobe','2026-04-25 17:24:38'),(11,'203','Double',2,8000.00,'AVAILABLE',2,'AC, WiFi, Personal Washroom, 2 Study Tables, 2 Wardrobes','2026-04-25 17:24:38'),(12,'204','Double',2,6500.00,'AVAILABLE',2,'Non-AC, WiFi, Shared Washroom, 2 Study Tables, 2 Wardrobes','2026-04-25 17:24:38'),(13,'205','Double',2,8500.00,'AVAILABLE',2,'AC, WiFi, Personal Washroom, Balcony, 2 Study Tables','2026-04-25 17:24:38'),(14,'206','Triple',3,7000.00,'AVAILABLE',2,'AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes','2026-04-25 17:24:38'),(15,'207','Triple',3,5500.00,'AVAILABLE',2,'Non-AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes','2026-04-25 17:24:38'),(16,'301','Single',1,10000.00,'AVAILABLE',3,'Premium AC, WiFi, Personal Washroom, TV, Sofa','2026-04-25 17:24:38'),(17,'302','Double',2,8500.00,'AVAILABLE',3,'AC, WiFi, Personal Washroom, Balcony, 2 Study Tables','2026-04-25 17:24:38'),(18,'303','Double',2,6500.00,'AVAILABLE',3,'Non-AC, WiFi, Shared Washroom, 2 Study Tables, 2 Wardrobes','2026-04-25 17:24:38'),(19,'304','Triple',3,7000.00,'AVAILABLE',3,'AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes','2026-04-25 17:24:38'),(20,'305','Dormitory',4,4000.00,'AVAILABLE',3,'Non-AC, WiFi, Shared Washroom, Bunk Beds, Lockers','2026-04-25 17:24:38');
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_ids`
--

DROP TABLE IF EXISTS `student_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_ids` (
  `id` int NOT NULL AUTO_INCREMENT,
  `resident_id` int NOT NULL,
  `student_id_number` varchar(50) NOT NULL,
  `issue_date` date NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `status` varchar(20) DEFAULT 'ISSUED',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_id_number` (`student_id_number`),
  KEY `resident_id` (`resident_id`),
  CONSTRAINT `student_ids_ibfk_1` FOREIGN KEY (`resident_id`) REFERENCES `residents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_ids`
--

LOCK TABLES `student_ids` WRITE;
/*!40000 ALTER TABLE `student_ids` DISABLE KEYS */;
INSERT INTO `student_ids` VALUES (1,1,'HS-2026-001','2026-04-01','2027-03-31','ISSUED','2026-04-25 17:33:21'),(2,2,'HS-2026-002','2026-04-01','2027-03-31','ISSUED','2026-04-25 17:33:21'),(3,3,'HS-2026-003','2026-04-02','2027-03-31','LOST','2026-04-25 17:33:21'),(4,4,'HS-2026-004','2026-04-02','2027-03-31','RENEWED','2026-04-25 17:33:21'),(5,5,'HS-2026-005','2026-04-03','2027-03-31','ISSUED','2026-04-25 17:33:21'),(6,6,'HS-2026-006','2026-04-03','2025-03-31','EXPIRED','2026-04-25 17:33:21'),(7,7,'HS-2026-007','2026-04-04','2027-03-31','ISSUED','2026-04-25 17:33:21'),(8,8,'HS-2026-008','2026-04-04','2027-03-31','LOST','2026-04-25 17:33:21'),(9,9,'HS-2026-009','2026-04-05','2027-03-31','RENEWED','2026-04-25 17:33:21'),(10,10,'HS-2026-010','2026-04-05','2025-03-31','EXPIRED','2026-04-25 17:33:21');
/*!40000 ALTER TABLE `student_ids` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin123','ADMIN','System Administrator','admin@hostel.com','2026-04-25 17:24:38'),(2,'aarav','student123','STUDENT','Aarav Sharma','aarav@email.com','2026-04-25 17:33:21'),(3,'vihaan','student123','STUDENT','Vihaan Patel','vihaan@email.com','2026-04-25 17:33:21'),(4,'ishaan','student123','STUDENT','Ishaan Gupta','ishaan@email.com','2026-04-25 17:33:21'),(5,'aditya','student123','STUDENT','Aditya Singh','aditya@email.com','2026-04-25 17:33:21'),(6,'krishna','student123','STUDENT','Krishna Rao','krishna@email.com','2026-04-25 17:33:21'),(7,'rohan','student123','STUDENT','Rohan Mehta','rohan@email.com','2026-04-25 17:33:21'),(8,'arjun','student123','STUDENT','Arjun Nair','arjun@email.com','2026-04-25 17:33:21'),(9,'dhruv','student123','STUDENT','Dhruv Kumar','dhruv@email.com','2026-04-25 17:33:21'),(10,'vivaan','student123','STUDENT','Vivaan Reddy','vivaan@email.com','2026-04-25 17:33:21'),(11,'ayaan','student123','STUDENT','Ayaan Khan','ayaan@email.com','2026-04-25 17:33:21'),(12,'warden','warden123','WARDEN','Chief Warden','warden@hostel.com','2026-04-25 18:09:08');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-03 16:59:47
