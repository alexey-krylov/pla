/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS geo;
DROP TABLE IF EXISTS channel_type;
DROP TABLE IF EXISTS bank_name;
DROP TABLE IF EXISTS bank_branch;
DROP TABLE IF EXISTS hcp_service;
DROP TABLE IF EXISTS region;

CREATE TABLE geo (
  GEO_ID VARCHAR(20) NOT NULL,
  PARENT_GEO_ID VARCHAR(20),
  GEO_TYPE VARCHAR(20) NOT NULL,
  GEO_DESCRIPTION VARCHAR(100) NOT NULL,
  PRIMARY KEY(GEO_ID)
);


CREATE TABLE channel_type (
  CHANNEL_CODE VARCHAR(20),
  CHANNEL_DESCRIPTION VARCHAR(50) NOT NULL UNIQUE,
  PRIMARY KEY(CHANNEL_CODE)
);


CREATE TABLE bank_name (
  BANK_CODE VARCHAR(20),
  BANK_NAME VARCHAR(50) NOT NULL UNIQUE,
  PRIMARY KEY(BANK_CODE)
);


CREATE TABLE bank_branch (
  BANK_CODE VARCHAR(20) NOT NULL,
  BRANCH VARCHAR(100) NOT NULL,
  `REGION_CODE` varchar(20) NOT NULL,
  SORT_CODE VARCHAR(100),
  PRIMARY KEY(SORT_CODE),
   FOREIGN KEY (BANK_CODE)
	REFERENCES bank_name(BANK_CODE)
);


CREATE TABLE hcp_service (
  HCP_SERVICE_CODE VARCHAR(20),
  HCP_DESCRIPTION VARCHAR(50) NOT NULL,
  HCP_TYPE VARCHAR(50) NOT NULL,
  ADDRESS VARCHAR(200),
  PROVINCE_GEO_ID VARCHAR(20) NOT NUll,
  TOWN_GEO_ID VARCHAR(20) NOT NULL,
  POSTAL_CODE_GEO_ID VARCHAR(20),
  WORK_PHONE BIGINT(13),
  EMAIL_ADDRESS VARCHAR(320),
  PRIMARY KEY(HCP_SERVICE_CODE),
  FOREIGN KEY (PROVINCE_GEO_ID)
	REFERENCES geo(GEO_ID),
  FOREIGN KEY (TOWN_GEO_ID)
	REFERENCES geo(GEO_ID)
);

DROP TABLE IF EXISTS benefit;
CREATE TABLE `benefit` (
  `benefit_id` varchar(255) NOT NULL,
  `status` varchar(255)  NOT NULL,
  `benefit_name` varchar(100)  NOT NULL,
  PRIMARY KEY (`benefit_id`),
  UNIQUE KEY `UNQ_BENEFIT_NAME` (`benefit_name`)
);

DROP TABLE IF EXISTS coverage;
CREATE TABLE `coverage` (
  `coverage_id` varchar(255) NOT NULL,
  `coverage_name` varchar(50)  NOT NULL,
  `description` varchar(150) DEFAULT NULL,
  `status` varchar(255)  NOT NULL,
  PRIMARY KEY (`coverage_id`),
  UNIQUE KEY `UNQ_COVERAGE_NAME` (`coverage_name`)
);

DROP TABLE IF EXISTS coverage_benefit;
CREATE TABLE `coverage_benefit` (
  `coverage_id` varchar(255) NOT NULL,
  `benefit_id` varchar(255) NOT NULL,
  UNIQUE KEY `UK_BENEFIT_ID` (`benefit_id`),
  KEY `FK_COVERAGE_ID` (`coverage_id`),
  CONSTRAINT `FK_COVERAGE_COVERAGE_ID` FOREIGN KEY (`coverage_id`) REFERENCES `coverage` (`coverage_id`),
  CONSTRAINT `FK_BENEFIT_BENEFIT_ID` FOREIGN KEY (`benefit_id`) REFERENCES `benefit` (`benefit_id`)
);

DROP TABLE IF EXISTS `branch`;
CREATE TABLE `branch`(
  `BRANCH_CODE` varchar(20) NOT NULL,
  `BRANCH` varchar(255) NOT NULL,
  `BRANCH_MANAGER` varchar(255) NOT NULL,
  `BRANCH_BDE` varchar(255) NOT NULL,
  PRIMARY KEY (`BRANCH_CODE`)
);

DROP TABLE IF EXISTS `region`;
CREATE TABLE `region`(
  `REGION_CODE` varchar(20) NOT NULL,
  `REGION` varchar(12) NOT NULL,
  `REGIONAL_MANAGER` varchar(255) NOT NULL,
  PRIMARY KEY (`REGION_CODE`),
  UNIQUE KEY `REGION` (`REGION`)
);

DROP TABLE IF EXISTS `team`;

CREATE TABLE `team`(
  `team_id` varchar(255) NOT NULL,
  `active` bit(1) DEFAULT NULL,
  `current_team_leader` varchar(255) DEFAULT NULL,
  `team_code` varchar(255) DEFAULT NULL,
  `team_name` varchar(255) DEFAULT NULL,
  `region_code` varchar(20) NOT NULL,
  `branch_code` varchar(20) NOT NULL,
  PRIMARY KEY (`team_id`),
  UNIQUE KEY `UNQ_TEAM_CODE_NAME` (`team_code`,`team_name`),
  KEY `FK_TEAM_REGION_REGION_CODE` (`region_code`),
  KEY `FK_TEAM_BRANCH_BRANCH_CODE` (`branch_code`),
  CONSTRAINT `FK_TEAM_BRANCH_BRANCH_CODE` FOREIGN KEY (`branch_code`) REFERENCES `branch` (`BRANCH_CODE`),
  CONSTRAINT `FK_TEAM_REGION_REGION_CODE` FOREIGN KEY (`region_code`) REFERENCES `region` (`REGION_CODE`)
);

DROP TABLE IF EXISTS team_team_leader_fulfillment;
CREATE TABLE `team_team_leader_fulfillment`(
  `team_id` varchar(255) NOT NULL,
  `from_date` date DEFAULT NULL,
  `employee_id` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `thru_date` date DEFAULT NULL,
  `team_leaders_order` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`team_id`,`team_leaders_order`),
  KEY `team_leaders_order` (`team_leaders_order`),
  CONSTRAINT `FK_TEAM_LEADER_FULFILLMENT` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`)
);

DROP TABLE IF EXISTS agent;
CREATE TABLE `agent` (
  `agent_id` int(11) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `employee_id` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `nrc_number` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `training_complete_on` date DEFAULT NULL,
  `agent_status` varchar(255) DEFAULT NULL,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `postal_code` int(11) NOT NULL,
  `province` varchar(255) DEFAULT NULL,
  `home_phone_number` int(11) NOT NULL,
  `mobile_number` int(11) NOT NULL,
  `work_phone_number` int(11) NOT NULL,
  `license_number` varchar(255) DEFAULT NULL,
  `override_commission_applicable` varchar(255) DEFAULT NULL,
  `physical_address_line1` varchar(255) DEFAULT NULL,
  `physical_address_line2` varchar(255) DEFAULT NULL,
  `physical_address_city` varchar(255) DEFAULT NULL,
  `physical_address_postal_code` int(11) DEFAULT NULL,
  `physical_address_province` varchar(255) DEFAULT NULL,
  `team_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`agent_id`)
);

DROP TABLE IF EXISTS agent_authorized_plan;
CREATE TABLE `agent_authorized_plan` (
  `agent_id` int(11) NOT NULL,
  `plan_id` varchar(255) DEFAULT NULL,
  KEY `FK_AGENT_PLAN_ID` (`agent_id`),
  CONSTRAINT `FK_AUTH_PLAN` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`agent_id`)
);


DROP TABLE IF EXISTS sum_assured;
CREATE TABLE `sum_assured` (
  `type` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS premium_payment;
CREATE TABLE `premium_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `payment_cut_off_age` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS policy_term;
CREATE TABLE `policy_term` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `max_maturity_age` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS plan_payment;
CREATE TABLE `plan_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `premium_payment_term_type` int(11) NOT NULL,
  `premium_payment_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_jc8gdw8f50sukxl8ldr98p2cu` (`premium_payment_id`),
  CONSTRAINT `FK_jc8gdw8f50sukxl8ldr98p2cu` FOREIGN KEY (`premium_payment_id`) REFERENCES `premium_payment` (`id`)
);
DROP TABLE IF EXISTS plan;
CREATE TABLE `plan` (
  `plan_id` varchar(255) NOT NULL,
  `last_event_sequence_number` bigint(20) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `plan_payment_id` bigint(20) DEFAULT NULL,
  `policy_term_id` bigint(20) DEFAULT NULL,
  `sum_assured_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`plan_id`),
  KEY `FK_r70nyaocgvvgcrwuqhvwh8v0u` (`plan_payment_id`),
  KEY `FK_31dua9hmubeiqdtvvb90u1pxm` (`policy_term_id`),
  KEY `FK_5i9s67duuj0127co5wmf9o5h` (`sum_assured_id`),
  CONSTRAINT `FK_5i9s67duuj0127co5wmf9o5h` FOREIGN KEY (`sum_assured_id`) REFERENCES `sum_assured` (`id`),
  CONSTRAINT `FK_31dua9hmubeiqdtvvb90u1pxm` FOREIGN KEY (`policy_term_id`) REFERENCES `policy_term` (`id`),
  CONSTRAINT `FK_r70nyaocgvvgcrwuqhvwh8v0u` FOREIGN KEY (`plan_payment_id`) REFERENCES `plan_payment` (`id`)
);
DROP TABLE IF EXISTS plan_coverage;
CREATE TABLE `plan_coverage` (
  `coverage_id` varchar(255) NOT NULL,
  `coverage_cover` int(11) NOT NULL,
  `coverage_type` int(11) NOT NULL,
  `deductible_amount` decimal(19,2) DEFAULT NULL,
  `deductible_percentage` decimal(19,2) DEFAULT NULL,
  `max_age` int(11) NOT NULL,
  `min_age` int(11) NOT NULL,
  `tax_applicable` bit(1) NOT NULL,
  `waiting_period` int(11) NOT NULL,
  `plan_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`coverage_id`),
  KEY `FK_n7h9fv72t4rkcfxy65nmwn88p` (`plan_id`),
  CONSTRAINT `FK_n7h9fv72t4rkcfxy65nmwn88p` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`plan_id`)
);

DROP TABLE IF EXISTS plan_coverage_benefit;
CREATE TABLE `plan_coverage_benefit` (
  `plan_coverage_id` varchar(255) NOT NULL,
  `benefit_id` varchar(255) DEFAULT NULL,
  `benefit_limit` decimal(19,2) DEFAULT NULL,
  `coverage_benefit_type` int(11) DEFAULT NULL,
  `defined_per` int(11) DEFAULT NULL,
  `max_limit` decimal(19,2) DEFAULT NULL,
  KEY `FK_8jd3iuqc7x0yb17aovswhs92n` (`plan_coverage_id`),
  CONSTRAINT `FK_8jd3iuqc7x0yb17aovswhs92n` FOREIGN KEY (`plan_coverage_id`) REFERENCES `plan_coverage` (`coverage_id`)
);


DROP TABLE IF EXISTS plan_payment_maturity_amounts;
CREATE TABLE `plan_payment_maturity_amounts` (
  `plan_payment_id` bigint(20) NOT NULL,
  `guaranteed_survival_benefit_amount` decimal(19,2) NOT NULL,
  `maturity_year` int(11) NOT NULL,
  KEY `FK_7qovxgs106k36s3y5pohdq3iw` (`plan_payment_id`),
  CONSTRAINT `FK_7qovxgs106k36s3y5pohdq3iw` FOREIGN KEY (`plan_payment_id`) REFERENCES `plan_payment` (`id`)
);


DROP TABLE IF EXISTS policy_term_valid_terms;
CREATE TABLE `policy_term_valid_terms` (
  `policy_term_id` bigint(20) NOT NULL,
  `valid_terms` int(11) DEFAULT NULL,
  KEY `FK_lqp4y9lbtc3yec8t3altqk935` (`policy_term_id`),
  CONSTRAINT `FK_lqp4y9lbtc3yec8t3altqk935` FOREIGN KEY (`policy_term_id`) REFERENCES `policy_term` (`id`)
);

DROP TABLE IF EXISTS premium_payment_valid_terms;
CREATE TABLE `premium_payment_valid_terms` (
  `premium_payment_id` bigint(20) NOT NULL,
  `valid_terms` int(11) DEFAULT NULL,
  KEY `FK_i4nq1052vfgxnmn4umabuox8y` (`premium_payment_id`),
  CONSTRAINT `FK_i4nq1052vfgxnmn4umabuox8y` FOREIGN KEY (`premium_payment_id`) REFERENCES `premium_payment` (`id`)
);

DROP TABLE IF EXISTS sum_assured_sum_insured_values;
CREATE TABLE `sum_assured_sum_insured_values` (
  `sum_assured_id` bigint(20) NOT NULL,
  `sum_insured_values` decimal(19,2) DEFAULT NULL,
  KEY `FK_8v9k7sydll2yrf86atgs5081u` (`sum_assured_id`),
  CONSTRAINT `FK_8v9k7sydll2yrf86atgs5081u` FOREIGN KEY (`sum_assured_id`) REFERENCES `sum_assured` (`id`)
);
DROP TABLE IF EXISTS `entity_sequence`;
CREATE TABLE `entity_sequence`(
  `sequence_id` INT(11) NOT NULL,
  `sequence_name` text NOT NULL,
  `sequence_number` DECIMAL(20,0) NOT NULL,
  `sequence_prefix` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`sequence_id`)
);


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;