DROP TABLE IF EXISTS coverage_benefit;
DROP TABLE IF EXISTS coverage;
DROP TABLE IF EXISTS benefit;
CREATE TABLE `benefit` (
  `benefit_id` varchar(255) NOT NULL,
  `status` varchar(255)  NOT NULL,
  `benefit_name` varchar(100)  NOT NULL,
  PRIMARY KEY (`benefit_id`),
  UNIQUE KEY `UNQ_BENEFIT_NAME` (`benefit_name`)
);

CREATE TABLE `coverage` (
  `coverage_id` varchar(255) NOT NULL,
  `coverage_name` varchar(50)  NOT NULL,
  `description` varchar(150) DEFAULT NULL,
  `status` varchar(255)  NOT NULL,
  PRIMARY KEY (`coverage_id`),
  UNIQUE KEY `UNQ_COVERAGE_NAME` (`coverage_name`)
);

CREATE TABLE `coverage_benefit` (
  `coverage_id` varchar(255) NOT NULL,
  `benefit_id` varchar(255) NOT NULL,
  UNIQUE KEY `UK_BENEFIT_ID` (`benefit_id`),
  KEY `FK_COVERAGE_ID` (`coverage_id`),
  CONSTRAINT `FK_COVERAGE_COVERAGE_ID` FOREIGN KEY (`coverage_id`) REFERENCES `coverage` (`coverage_id`),
  CONSTRAINT `FK_BENEFIT_BENEFIT_ID` FOREIGN KEY (`benefit_id`) REFERENCES `benefit` (`benefit_id`)
);
DROP TABLE IF EXISTS team_team_leader_fulfillment;
DROP TABLE IF EXISTS team;
CREATE TABLE `team` (
  `team_id` varchar(255) NOT NULL,
  `active` bit(1) DEFAULT NULL,
  `current_team_leader` varchar(255) DEFAULT NULL,
  `team_code` varchar(255) DEFAULT NULL,
  `team_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`team_id`),
  UNIQUE KEY `UNQ_TEAM_CODE_NAME` (`team_code`,`team_name`)
);
CREATE TABLE `team_team_leader_fulfillment` (
  `team_id` varchar(255) NOT NULL,
  `from_date` date DEFAULT NULL,
  `employee_id` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `thru_date` date DEFAULT NULL,
  `team_leaders_order` int(11) NOT NULL,
  PRIMARY KEY (`team_id`,`team_leaders_order`),
  CONSTRAINT `FK_TEAM_LEADER_FULFILLMENT` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`)
);