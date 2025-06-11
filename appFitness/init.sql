/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `fitnessApp` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `fitnessApp`;

CREATE TABLE IF NOT EXISTS `Aluno` (
  `id` int NOT NULL,
  `professor_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `professor_id` (`professor_id`),
  CONSTRAINT `Aluno_ibfk_1` FOREIGN KEY (`id`) REFERENCES `User` (`id`),
  CONSTRAINT `Aluno_ibfk_2` FOREIGN KEY (`professor_id`) REFERENCES `Professor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `BodyMetrics` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `height` float DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `bmi` float DEFAULT NULL,
  `bodyFatPercentage` float DEFAULT NULL,
  `updatedAt` datetime(6) NOT NULL,
  `metricType` enum('IMPERIAL','METRIC') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `BodyMetrics_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `Exercise` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `type` ENUM('STRENGTH', 'CARDIO', 'BODYWEIGHT') DEFAULT NULL,
  `muscleGroup` ENUM('CHEST', 'BACK', 'LEGS', 'SHOULDERS', 'ARMS', 'CORE') DEFAULT NULL,
  `videoURL` varchar(255) DEFAULT NULL,
  `imageURL` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `ExerciseData` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workoutPlan_id` int DEFAULT NULL,
  `exercise_id` int DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `isDeleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `workoutPlan_id` (`workoutPlan_id`),
  KEY `exercise_id` (`exercise_id`),
  CONSTRAINT `ExerciseData_ibfk_1` FOREIGN KEY (`workoutPlan_id`) REFERENCES `WorkoutPlan` (`id`),
  CONSTRAINT `ExerciseData_ibfk_2` FOREIGN KEY (`exercise_id`) REFERENCES `Exercise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `ExerciseDataSet` (
  `id` int NOT NULL AUTO_INCREMENT,
  `exerciseData_id` int DEFAULT NULL,
  `setNumber` int DEFAULT NULL,
  `weightPlanned` float DEFAULT NULL,
  `repsPlanned` int DEFAULT NULL,
  `restTimeSuggested` float DEFAULT NULL,
  `restTimeSuggestion` decimal(21,0) DEFAULT NULL,
  `restTimeSugested` float DEFAULT NULL,
  `isDeleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `exerciseData_id` (`exerciseData_id`),
  CONSTRAINT `ExerciseDataSet_ibfk_1` FOREIGN KEY (`exerciseData_id`) REFERENCES `ExerciseData` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `ExerciseExecution` (
  `id` int NOT NULL AUTO_INCREMENT,
  `exerciseData_id` int DEFAULT NULL,
  `workoutExecution_id` int DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `startTime` timestamp NULL DEFAULT NULL,
  `endTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `exerciseData_id` (`exerciseData_id`),
  KEY `workoutExecution_id` (`workoutExecution_id`),
  CONSTRAINT `ExerciseExecution_ibfk_1` FOREIGN KEY (`exerciseData_id`) REFERENCES `ExerciseData` (`id`),
  CONSTRAINT `ExerciseExecution_ibfk_2` FOREIGN KEY (`workoutExecution_id`) REFERENCES `WorkoutExecution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `ExerciseExecutionSet` (
  `id` int NOT NULL AUTO_INCREMENT,
  `exerciseExecution_id` int DEFAULT NULL,
  `setNumber` int DEFAULT NULL,
  `repsPerformed` int DEFAULT NULL,
  `weightPerformed` float DEFAULT NULL,
  `restTimePerformed` float DEFAULT NULL,
  `resTimePerformed` decimal(21,0) DEFAULT NULL,
  `set_data_id` int DEFAULT NULL,
  `isCompleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `exerciseExecution_id` (`exerciseExecution_id`),
  KEY `FK4hxa7elpu27p227jupmb2gx2x` (`set_data_id`),
  CONSTRAINT `ExerciseExecutionSet_ibfk_1` FOREIGN KEY (`exerciseExecution_id`) REFERENCES `ExerciseExecution` (`id`),
  CONSTRAINT `FK4hxa7elpu27p227jupmb2gx2x` FOREIGN KEY (`set_data_id`) REFERENCES `ExerciseDataSet` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `Notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `priority` int DEFAULT NULL,
  `createdAt` datetime(6) NOT NULL,
  `read` tinyint(1) DEFAULT NULL,
  `receiver_id` int DEFAULT NULL,
  `type` enum('WORKOUT_CREATED','WORKOUT_UPDATE','PROFESSOR_ASSIGNED','PROFESSOR_UNASSIGNED','PROFESSOR_NOTIFY','SCHEDULED_WORKOUT','WORKOUT_FINISHED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `receiver` int NOT NULL,
  `is_read` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `receiver_id` (`receiver_id`),
  KEY `FKhsm4iel2mjxat7jaixw34k4ut` (`receiver`),
  CONSTRAINT `FKhsm4iel2mjxat7jaixw34k4ut` FOREIGN KEY (`receiver`) REFERENCES `User` (`id`),
  CONSTRAINT `Notification_ibfk_1` FOREIGN KEY (`receiver_id`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `Professor` (
  `id` int NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `Professor_ibfk_1` FOREIGN KEY (`id`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `User` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `metricType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `WorkoutExecution` (
  `id` int NOT NULL AUTO_INCREMENT,
  `executionDate` date DEFAULT NULL,
  `workout_id` int DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `feedback` text,
  `startTime` timestamp NULL DEFAULT NULL,
  `endTime` timestamp NULL DEFAULT NULL,
  `feeback` varchar(255) DEFAULT NULL,
  `user_id` int NOT NULL,
  `workout_plan_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `workout_id` (`workout_id`),
  KEY `FKlv7ahcsxdalvhcrkjdlpm58hi` (`user_id`),
  KEY `FKnq3cblq4nrr7xe8bqmuon4wr5` (`workout_plan_id`),
  CONSTRAINT `FKlv7ahcsxdalvhcrkjdlpm58hi` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`),
  CONSTRAINT `FKnq3cblq4nrr7xe8bqmuon4wr5` FOREIGN KEY (`workout_plan_id`) REFERENCES `WorkoutPlan` (`id`),
  CONSTRAINT `WorkoutExecution_ibfk_1` FOREIGN KEY (`workout_id`) REFERENCES `WorkoutPlan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `WorkoutPlan` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `createdBy` int DEFAULT NULL,
  `owner_id` int DEFAULT NULL,
  `scheduleType` enum('Fixed','Free') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `scheduleDays` text,
  `description` varchar(255) DEFAULT NULL,
  `updatedAt` date DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `owner_id` (`owner_id`),
  KEY `createdBy` (`createdBy`),
  CONSTRAINT `WorkoutPlan_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `User` (`id`),
  CONSTRAINT `WorkoutPlan_ibfk_2` FOREIGN KEY (`createdBy`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `workout_plan_scheduled_days` (
  `workout_plan_id` int NOT NULL,
  `scheduledDays` enum('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') DEFAULT NULL,
  KEY `FKdh14o7teks2arylo0ju0oxlnq` (`workout_plan_id`),
  CONSTRAINT `FKdh14o7teks2arylo0ju0oxlnq` FOREIGN KEY (`workout_plan_id`) REFERENCES `WorkoutPlan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Insert initial data into Exercise table
-- CHEST
INSERT INTO `Exercise` (`name`, `description`, `type`, `muscleGroup`, `videoURL`, `imageURL`) VALUES
('Bench Press', 'Classic barbell chest exercise.', 'STRENGTH', 'CHEST', NULL, NULL),
('Incline Dumbbell Press', 'Targets the upper chest.', 'STRENGTH', 'CHEST', NULL, NULL),
('Push-Up', 'Bodyweight chest exercise.', 'BODYWEIGHT', 'CHEST', NULL, NULL),
('Cable Fly', 'Isolates chest muscles.', 'STRENGTH', 'CHEST', NULL, NULL),
('Chest Dip', 'Targets lower chest and triceps.', 'BODYWEIGHT', 'CHEST', NULL, NULL);

-- BACK
INSERT INTO `Exercise` (`name`, `description`, `type`, `muscleGroup`, `videoURL`, `imageURL`) VALUES
('Deadlift', 'Compound back strength movement.', 'STRENGTH', 'BACK', NULL, NULL),
('Pull-Up', 'Bodyweight back exercise.', 'BODYWEIGHT', 'BACK', NULL, NULL),
('Seated Row', 'Machine exercise for back.', 'STRENGTH', 'BACK', NULL, NULL),
('Lat Pulldown', 'Targets lats directly.', 'STRENGTH', 'BACK', NULL, NULL),
('T-Bar Row', 'Strengthens the middle back.', 'STRENGTH', 'BACK', NULL, NULL);

-- LEGS
INSERT INTO `Exercise` (`name`, `description`, `type`, `muscleGroup`, `videoURL`, `imageURL`) VALUES
('Squat', 'Fundamental leg strength exercise.', 'STRENGTH', 'LEGS', NULL, NULL),
('Leg Press', 'Machine-based leg workout.', 'STRENGTH', 'LEGS', NULL, NULL),
('Lunges', 'Unilateral leg movement.', 'BODYWEIGHT', 'LEGS', NULL, NULL),
('Step-Up', 'Bodyweight leg exercise.', 'BODYWEIGHT', 'LEGS', NULL, NULL),
('Running', 'Cardio leg exercise.', 'CARDIO', 'LEGS', NULL, NULL);

-- SHOULDERS
INSERT INTO `Exercise` (`name`, `description`, `type`, `muscleGroup`, `videoURL`, `imageURL`) VALUES
('Overhead Press', 'Strength shoulder press.', 'STRENGTH', 'SHOULDERS', NULL, NULL),
('Lateral Raise', 'Isolates lateral deltoid.', 'STRENGTH', 'SHOULDERS', NULL, NULL),
('Front Raise', 'Isolates front deltoid.', 'STRENGTH', 'SHOULDERS', NULL, NULL),
('Handstand Push-Up', 'Bodyweight shoulder press.', 'BODYWEIGHT', 'SHOULDERS', NULL, NULL),
('Battle Ropes', 'Cardio shoulder workout.', 'CARDIO', 'SHOULDERS', NULL, NULL);

-- ARMS
INSERT INTO `Exercise` (`name`, `description`, `type`, `muscleGroup`, `videoURL`, `imageURL`) VALUES
('Bicep Curl', 'Isolation exercise for biceps.', 'STRENGTH', 'ARMS', NULL, NULL),
('Triceps Pushdown', 'Isolation for triceps.', 'STRENGTH', 'ARMS', NULL, NULL),
('Hammer Curl', 'Targets biceps and forearms.', 'STRENGTH', 'ARMS', NULL, NULL),
('Diamond Push-Up', 'Bodyweight triceps exercise.', 'BODYWEIGHT', 'ARMS', NULL, NULL),
('Jump Rope', 'Cardio arm endurance.', 'CARDIO', 'ARMS', NULL, NULL);

-- CORE
INSERT INTO `Exercise` (`name`, `description`, `type`, `muscleGroup`, `videoURL`, `imageURL`) VALUES
('Plank', 'Isometric core stabilization.', 'BODYWEIGHT', 'CORE', NULL, NULL),
('Crunches', 'Bodyweight core exercise.', 'BODYWEIGHT', 'CORE', NULL, NULL),
('Russian Twist', 'Rotational core movement.', 'BODYWEIGHT', 'CORE', NULL, NULL),
('Hanging Leg Raise', 'Targets lower abs.', 'BODYWEIGHT', 'CORE', NULL, NULL),
('Mountain Climbers', 'Cardio & core workout.', 'CARDIO', 'CORE', NULL, NULL);


/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
