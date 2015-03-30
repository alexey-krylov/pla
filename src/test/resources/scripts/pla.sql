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
DROP VIEW IF EXISTS `agent_team_branch_view`;
DROP VIEW IF EXISTS `region_region_manger_fulfilment_view`;
DROP VIEW IF EXISTS `active_team_region_branch_view`;
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
DROP TABLE IF EXISTS coverage_benefit;
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


CREATE TABLE `coverage_benefit` (
  `coverage_id` varchar(255) NOT NULL,
  `benefit_id` varchar(255) NOT NULL,
  KEY `FK_COVERAGE_ID` (`coverage_id`)
);

DROP TABLE IF EXISTS `branch`;
CREATE TABLE `branch`(
  `branch_code` varchar(255) NOT NULL,
  `branch_bde` varchar(255) DEFAULT NULL,
  `branch_manager` varchar(255) DEFAULT NULL,
  `branch_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`branch_code`)
);

DROP TABLE IF EXISTS `region`;
CREATE TABLE `region`(
  `region_code` varchar(255) NOT NULL,
  `region_name` varchar(255) DEFAULT NULL,
  `regional_manager` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`region_code`),
  UNIQUE KEY `UNQ_REGION_CODE_NAME` (`region_code`,`region_name`)
);

DROP TABLE IF EXISTS `region_branch`;
CREATE TABLE `region_branch`(
  `region_code` varchar(255) NOT NULL,
  `branch_code` varchar(255) NOT NULL,
  PRIMARY KEY (`region_code`,`branch_code`),
  UNIQUE KEY `UK_n48d3jv1a5x3x5wh2wqv6sebr` (`branch_code`),
  CONSTRAINT `FK_1knyc3q94ravrw1qwm7xnx73u` FOREIGN KEY (`region_code`) REFERENCES `region` (`region_code`),
  CONSTRAINT `FK_n48d3jv1a5x3x5wh2wqv6sebr` FOREIGN KEY (`branch_code`) REFERENCES `branch` (`branch_code`)
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

DROP TABLE IF EXISTS region_manager_fulfillment;
CREATE TABLE `region_manager_fulfillment`(
  `region_code` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `from_date` date DEFAULT NULL,
  `thru_date` date DEFAULT NULL,
  `employee_id` varchar(255) NULL,
  PRIMARY KEY (`region_Code`,`employee_id`),
  CONSTRAINT `FK_REGION_CODE_REGION_MANAGER_FULFILLMENT_REGION_CODE` FOREIGN KEY (`region_Code`) REFERENCES `region` (`region_code`)
) ;


DROP TABLE IF EXISTS agent_authorized_plan;
CREATE TABLE `agent_authorized_plan` (
  `agent_id` varchar(255) NOT NULL,
  `plan_id` varchar(255) DEFAULT NULL,
  KEY `FK_fb90q6b5wf5iswifmkro1bfr8` (`agent_id`),
  CONSTRAINT `FK_fb90q6b5wf5iswifmkro1bfr8` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`agent_id`)
);

DROP TABLE IF EXISTS agent;
CREATE TABLE `agent`(
    `agent_id` varchar(255) NOT NULL,
    `designation_code` varchar(255) DEFAULT NULL,
    `designation_name` varchar(255) DEFAULT NULL,
    `employee_id` varchar(255) DEFAULT NULL,
    `first_name` varchar(255) DEFAULT NULL,
    `last_name` varchar(255) DEFAULT NULL,
    `nrc_number` int(11) DEFAULT NULL,
    `title` varchar(255) DEFAULT NULL,
    `training_complete_on` date DEFAULT NULL,
    `agent_status` varchar(255) DEFAULT NULL,
    `channel_code` varchar(255) DEFAULT NULL,
    `channel_name` varchar(255) DEFAULT NULL,
    `address_line1` varchar(255) DEFAULT NULL,
    `address_line2` varchar(255) DEFAULT NULL,
    `email` varchar(255) DEFAULT NULL,
    `city` varchar(255) DEFAULT NULL,
    `postal_code` int(11) DEFAULT NULL,
    `province` varchar(255) DEFAULT NULL,
    `home_phone_number` varchar(255) DEFAULT NULL,
    `mobile_number` varchar(255) DEFAULT NULL,
    `work_phone_number` varchar(255) DEFAULT NULL,
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
CREATE TABLE `entity_sequence` (
  `sequence_id` int(11) NOT NULL,
  `sequence_name` text NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `sequence_prefix` varchar(255) NOT NULL,
  PRIMARY KEY (`sequence_id`)
);

CREATE  VIEW `agent_team_branch_view` AS
(SELECT  `agent_id` AS agentId,A.title AS title,  `designation_code` AS designationCode,  `designation_name` AS designationName,
  A.employee_id AS employeeId,A.first_name AS firstName,  A.`last_name` AS lastName,  `nrc_number` AS nrcNumber,  `training_complete_on` AS trainingCompletedOn,
  agent_status AS agentStatus,channel_code AS channelCode,  channel_name AS channelName,  address_line1 AS addressLine1,  address_line2 AS addressLine2,
  email,  A.city AS cityCode,(SELECT geo_description  FROM  geo WHERE geo_type = 'CITY' AND geo_id = A.city) AS cityName,
  postal_code AS postalCode,  A.province AS provinceCode,  (SELECT geo_description FROM geo WHERE geo_type = 'PROVINCE' AND geo_id = A.province) AS provinceName,
  home_phone_number AS homePhoneNumber,mobile_number AS mobileNumber,work_phone_number AS workPhoneNumber,license_number AS licenseNumber,`override_commission_applicable` AS overrideCommissionApplicable,
  physical_address_line1 AS physicalAddressLine1, physical_address_line2 AS physicalAddressLine2, physical_address_city AS physicalAddressCityCode,
  (SELECT geo_description FROM geo WHERE geo_type = 'CITY' AND geo_id = A.physical_address_city) AS physicalAddressCityName,physical_address_postal_code AS physicalAddressPostalCode,
  physical_address_province AS physicalAddressProvinceCode,(SELECT geo_description FROM geo WHERE geo_type = 'PROVINCE' AND geo_id = A.physical_address_province) AS physicalAddressProvinceName,
  A.team_id AS teamId,TF.employee_id AS teamLeaderId,TF.first_name AS teamLeaderFirstName,TF.last_name AS teamLeaderLastName,T.team_code AS teamCode,
  T.team_name AS teamName, R.region_code AS regionCode,R.region_name AS regionName,B.branch_code AS branchCode, B.branch_name AS branchName
   FROM  agent A  LEFT JOIN team T    ON A.team_id = T.`team_id`  LEFT JOIN `team_team_leader_fulfillment` TF    ON T.`team_id` = TF.`team_id`
    AND T.`current_team_leader` = TF.`employee_id`  LEFT JOIN region R  ON T.region_code = R.REGION_CODE LEFT JOIN branch B ON T.branch_code = B.branch_code );


CREATE VIEW `region_region_manger_fulfilment_view` AS
(SELECT R.region_code AS regionCode,R.region_name AS regionName,RF.first_name AS regionalManagerFirstName,RF.last_name AS regionalManagerLastName FROM region R LEFT JOIN `region_manager_fulfillment` RF
ON R.region_code = RF.region_code AND R.regional_manager=RF.employee_id);

CREATE VIEW `active_team_region_branch_view` AS
(SELECT T.team_id AS teamId,T.team_name AS teamName,TF.first_name AS leaderFirstName,TF.last_name AS leaderLastName,R.region_name AS regionName,
B.branch_name AS branchName FROM team T LEFT JOIN `team_team_leader_fulfillment` TF ON T.`team_id`=TF.team_id AND T.`current_team_leader`=TF.employee_id
LEFT JOIN `branch` B ON T.`branch_code`=B.`BRANCH_CODE` LEFT JOIN region R ON T.`region_code`=R.`REGION_CODE` WHERE T.`active`='1');

DROP TABLE IF EXISTS `mandatory_document`;
CREATE TABLE `mandatory_document` (
  `id` varchar(255) NOT NULL AUTO_INCREMENT,
  `coverage_id` varchar(255) DEFAULT NULL,
  `plan_id` varchar(255) DEFAULT NULL,
  `process` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `mandatory_document_documents`;
CREATE TABLE `mandatory_document_documents` (
  `mandatory_document_id` varchar(255) NOT NULL,
  `documents` varchar(255) DEFAULT NULL,
  KEY `FK_fl10nqnith5gv7fsqsfolbwsp` (`mandatory_document_id`),
  CONSTRAINT `FK_fl10nqnith5gv7fsqsfolbwsp` FOREIGN KEY (`mandatory_document_id`) REFERENCES `mandatory_document` (`id`)
) ;


/*Table structure for document */
DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
  `document_name` varchar(255) NOT NULL,
  `document_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`document_name`)
);


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;