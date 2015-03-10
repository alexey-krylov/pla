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