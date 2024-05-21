-- MySQL dump 10.13  Distrib 5.7.26, for osx10.10 (x86_64)
--
-- Host: 127.0.0.1    Database: catering
-- ------------------------------------------------------
-- Server version 5.7.26

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
-- Table structure for table `Roles`
--

CREATE TABLE `Roles` (
  `id` char(1) NOT NULL,
  `role` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Roles`
--

LOCK TABLES `Roles` WRITE;
/*!40000 ALTER TABLE `Roles` DISABLE KEYS */;
INSERT INTO `Roles` VALUES ('c','Cuoco'),('h','Chef'),('o','Organizzatore'),('s','Servizio');
/*!40000 ALTER TABLE `Roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES 
(1,'Carlin'),
(2,'Lidia'),
(3,'Tony'),
(4,'Marinella'),
(5,'Guido'),
(6,'Antonietta'),
(7,'Paola'),
(8,'Silvia'),
(9,'Marco'),
(10,'Piergiorgio');
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserRoles`
--

CREATE TABLE `UserRoles` (
  `user_id` int(11) NOT NULL,
  `role_id` char(1) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `Users`(`id`),
  FOREIGN KEY (`role_id`) REFERENCES `Roles`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `UserRoles`
--

LOCK TABLES `UserRoles` WRITE;
/*!40000 ALTER TABLE `UserRoles` DISABLE KEYS */;
INSERT INTO `UserRoles` VALUES 
(1,'o'),
(2,'h'),(2,'o'),
(3,'h'),
(4,'c'),(4,'h'),
(5,'c'),
(6,'c'),
(7,'c'),(7,'s'),
(8,'s'),
(9,'s'),
(10,'s');
/*!40000 ALTER TABLE `UserRoles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Turns`
--

CREATE TABLE `Turns` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL, -- 'Service' or 'Cooking'
  `duration` int(11) NOT NULL,
  `location` varchar(255) NOT NULL,
  `deadline` datetime NOT NULL,
  `date` datetime NOT NULL,
  `is_recurring` boolean DEFAULT FALSE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Turns`
--

LOCK TABLES `Turns` WRITE;
/*!40000 ALTER TABLE `Turns` DISABLE KEYS */;
INSERT INTO `Turns` (`type`, `duration`, `location`, `deadline`, `date`, `is_recurring`) VALUES
('Cooking', 180, 'Kitchen A', '2024-05-30 12:00:00', '2024-06-01 08:00:00', FALSE),
('Service', 240, 'Hall B', '2024-05-30 18:00:00', '2024-06-01 12:00:00', TRUE);
/*!40000 ALTER TABLE `Turns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tasks`
--

CREATE TABLE `Tasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `assigned_to` int(11) DEFAULT NULL,
  `role_required` char(1) DEFAULT NULL,
  `turn_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `portions` int(11) DEFAULT NULL,
  `due_time` datetime DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Pending',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`assigned_to`) REFERENCES `Users`(`id`),
  FOREIGN KEY (`role_required`) REFERENCES `Roles`(`id`),
  FOREIGN KEY (`turn_id`) REFERENCES `Turns`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `TaskAssignments`
--

CREATE TABLE `TaskAssignments` (
  `task_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `assigned_date` datetime NOT NULL,
  PRIMARY KEY (`task_id`, `user_id`),
  FOREIGN KEY (`task_id`) REFERENCES `Tasks`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `Users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Tasks`
--

LOCK TABLES `Tasks` WRITE;
/*!40000 ALTER TABLE `Tasks` DISABLE KEYS */;
INSERT INTO `Tasks` (`description`, `assigned_to`, `role_required`, `turn_id`, `quantity`, `portions`, `due_time`, `status`) VALUES
('Prepare appetizers', 4, 'c', 1, 50, 10, '2024-06-01 07:00:00', 'Pending'),
('Serve main course', 8, 's', 2, 100, 20, '2024-06-01 13:00:00', 'Pending');
/*!40000 ALTER TABLE `Tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `TaskAssignments`
--

LOCK TABLES `TaskAssignments` WRITE;
/*!40000 ALTER TABLE `TaskAssignments` DISABLE KEYS */;
INSERT INTO `TaskAssignments` (`task_id`, `user_id`, `assigned_date`) VALUES
(1, 4, '2024-05-20 10:00:00'),
(2, 8, '2024-05-21 11:00:00');
/*!40000 ALTER TABLE `TaskAssignments` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
