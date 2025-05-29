CREATE TABLE `User` (
                        `id` INTEGER AUTO_INCREMENT,
                        `name` VARCHAR(255),
                        `email` VARCHAR(255) UNIQUE,
                        `password` VARCHAR(255),
                        `metricType` VARCHAR(255),
                        PRIMARY KEY(`id`)
);


CREATE TABLE `Professor` (
                             `id` INTEGER,
                             PRIMARY KEY(`id`)
);


CREATE TABLE `Aluno` (
                         `id` INTEGER,
                         `professor_id` INTEGER,
                         PRIMARY KEY(`id`)
);


CREATE TABLE `BodyMetrics` (
                               `id` INTEGER AUTO_INCREMENT,
                               `user_id` INTEGER,
                               `height` FLOAT,
                               `weight` FLOAT,
                               `bmi` FLOAT,
                               `bodyFatPercentage` FLOAT,
                               `updatedAt` DATE,
                               PRIMARY KEY(`id`)
);


CREATE TABLE `Notification` (
                                `id` INTEGER AUTO_INCREMENT,
                                `title` VARCHAR(255),
                                `message` TEXT,
                                `priority` INTEGER,
                                `createdAt` DATE,
                                `read` BOOLEAN,
                                `receiver_id` INTEGER,
                                PRIMARY KEY(`id`)
);


CREATE TABLE `WorkoutPlan` (
                               `id` INTEGER AUTO_INCREMENT,
                               `name` VARCHAR(255),
                               `createdBy` INTEGER,
                               `owner_id` INTEGER,
                               `scheduleType` ENUM('Fixed', 'Free'),
                               `scheduleDays` TEXT,
                               `description` TEXT(65535),
                               `updatedAt` DATE,
                               PRIMARY KEY(`id`)
);


CREATE TABLE `Exercise` (
                            `id` INTEGER AUTO_INCREMENT,
                            `name` VARCHAR(255),
                            `description` TEXT(65535),
                            `type` VARCHAR(255),
                            `muscleGroup` VARCHAR(255),
                            `videoURL` TEXT,
                            `imageURL` TEXT(65535),
                            PRIMARY KEY(`id`)
);


CREATE TABLE `ExerciseData` (
                                `id` INTEGER AUTO_INCREMENT,
                                `workoutPlan_id` INTEGER,
                                `exercise_id` INTEGER,
                                `note` VARCHAR(255),
                                PRIMARY KEY(`id`)
);


CREATE TABLE `ExerciseDataSet` (
                                   `id` INTEGER AUTO_INCREMENT,
                                   `exerciseData_id` INTEGER,
                                   `setNumber` INTEGER,
                                   `weightPlanned` FLOAT,
                                   `repsPlanned` INTEGER,
                                   `restTimeSuggested` FLOAT,
                                   PRIMARY KEY(`id`)
);


CREATE TABLE `WorkoutExecution` (
                                    `id` INTEGER AUTO_INCREMENT,
                                    `executionDate` DATE,
                                    `workout_id` INTEGER,
                                    `status` ENUM('COMPLETED', 'MISSED'),
                                    `feedback` TEXT,
                                    `startTime` TIMESTAMP,
                                    `endTime` TIMESTAMP,
                                    PRIMARY KEY(`id`)
);


CREATE TABLE `ExerciseExecution` (
                                     `id` INTEGER AUTO_INCREMENT,
                                     `exerciseData_id` INTEGER,
                                     `workoutExecution_id` INTEGER,
                                     `status` ENUM('COMPLETED', 'MISSED'),
                                     `startTime` TIMESTAMP,
                                     `endTime` TIMESTAMP,
                                     PRIMARY KEY(`id`)
);


CREATE TABLE `ExerciseExecutionSet` (
                                        `id` INTEGER AUTO_INCREMENT,
                                        `exerciseExecution_id` INTEGER,
                                        `setNumber` INTEGER,
                                        `repsPerformed` INTEGER,
                                        `weightPerformed` FLOAT,
                                        `restTimePerformed` FLOAT,
                                        PRIMARY KEY(`id`)
);


ALTER TABLE `Professor`
    ADD FOREIGN KEY(`id`) REFERENCES `User`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Aluno`
    ADD FOREIGN KEY(`id`) REFERENCES `User`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Aluno`
    ADD FOREIGN KEY(`professor_id`) REFERENCES `Professor`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `BodyMetrics`
    ADD FOREIGN KEY(`user_id`) REFERENCES `User`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Notification`
    ADD FOREIGN KEY(`receiver_id`) REFERENCES `User`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `WorkoutPlan`
    ADD FOREIGN KEY(`owner_id`) REFERENCES `User`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `ExerciseData`
    ADD FOREIGN KEY(`workoutPlan_id`) REFERENCES `WorkoutPlan`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `ExerciseData`
    ADD FOREIGN KEY(`exercise_id`) REFERENCES `Exercise`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `ExerciseDataSet`
    ADD FOREIGN KEY(`exerciseData_id`) REFERENCES `ExerciseData`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `WorkoutExecution`
    ADD FOREIGN KEY(`workout_id`) REFERENCES `WorkoutPlan`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `ExerciseExecution`
    ADD FOREIGN KEY(`exerciseData_id`) REFERENCES `ExerciseData`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `ExerciseExecution`
    ADD FOREIGN KEY(`workoutExecution_id`) REFERENCES `WorkoutExecution`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `ExerciseExecutionSet`
    ADD FOREIGN KEY(`exerciseExecution_id`) REFERENCES `ExerciseExecution`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `WorkoutPlan`
    ADD FOREIGN KEY(`createdBy`) REFERENCES `User`(`id`)
        ON UPDATE NO ACTION ON DELETE NO ACTION;