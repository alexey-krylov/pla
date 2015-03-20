USE PLAJOBS;
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;
CREATE TABLE QRTZ_JOB_DETAILS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    SCHED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);
COMMIT;

USE PLA;

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

/*Table structure for table `agent` */

DROP TABLE IF EXISTS `agent`;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `agent` */

/*Table structure for table `agent_authorized_plan` */

DROP TABLE IF EXISTS `agent_authorized_plan`;

CREATE TABLE `agent_authorized_plan` (
  `agent_id` int(11) NOT NULL,
  `plan_id` varchar(255) DEFAULT NULL,
  KEY `FK_AGENT_PLAN_ID` (`agent_id`),
  CONSTRAINT `FK_AUTH_PLAN` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `agent_authorized_plan` */

/*Table structure for table `association_value_entry` */

DROP TABLE IF EXISTS `association_value_entry`;

CREATE TABLE `association_value_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `association_key` varchar(255) DEFAULT NULL,
  `association_value` varchar(255) DEFAULT NULL,
  `saga_id` varchar(255) DEFAULT NULL,
  `saga_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `association_value_entry` */

/*Table structure for table `bank_branch` */

DROP TABLE IF EXISTS `bank_branch`;

CREATE TABLE `bank_branch` (
  `BANK_CODE` varchar(20) NOT NULL,
  `BRANCH` varchar(100) NOT NULL,
  `SORT_CODE` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`SORT_CODE`),
  KEY `BANK_CODE` (`BANK_CODE`),
  CONSTRAINT `bank_branch_ibfk_1` FOREIGN KEY (`BANK_CODE`) REFERENCES `bank_name` (`BANK_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `bank_branch` */

/*Table structure for table `bank_name` */

DROP TABLE IF EXISTS `bank_name`;

CREATE TABLE `bank_name` (
  `BANK_CODE` varchar(20) NOT NULL DEFAULT '',
  `BANK_NAME` varchar(50) NOT NULL,
  PRIMARY KEY (`BANK_CODE`),
  UNIQUE KEY `BANK_NAME` (`BANK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `bank_name` */

/*Table structure for table `benefit` */

DROP TABLE IF EXISTS `benefit`;
CREATE TABLE `benefit`(
  `benefit_id` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `benefit_name` varchar(100) NOT NULL,
  PRIMARY KEY (`benefit_id`),
  UNIQUE KEY `UNQ_BENEFIT_NAME` (`benefit_name`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `benefit` */

/*Table structure for table `channel_type` */

DROP TABLE IF EXISTS `channel_type`;

CREATE TABLE `channel_type` (
  `CHANNEL_CODE` varchar(20) NOT NULL DEFAULT '',
  `CHANNEL_DESCRIPTION` varchar(50) NOT NULL,
  PRIMARY KEY (`CHANNEL_CODE`),
  UNIQUE KEY `CHANNEL_DESCRIPTION` (`CHANNEL_DESCRIPTION`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `channel_type` */

/*Table structure for table `coverage` */

DROP TABLE IF EXISTS `coverage`;

CREATE TABLE `coverage` (
  `coverage_id` varchar(255) NOT NULL,
  `coverage_name` varchar(50) NOT NULL,
  `description` varchar(150) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`coverage_id`),
  UNIQUE KEY `UNQ_COVERAGE_NAME` (`coverage_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `coverage` */

/*Table structure for table `coverage_benefit` */

DROP TABLE IF EXISTS `coverage_benefit`;

CREATE TABLE `coverage_benefit` (
  `coverage_id` varchar(255) NOT NULL,
  `benefit_id` varchar(255) NOT NULL,
  UNIQUE KEY `UK_BENEFIT_ID` (`benefit_id`),
  KEY `FK_COVERAGE_ID` (`coverage_id`),
  CONSTRAINT `FK_COVERAGE_COVERAGE_ID` FOREIGN KEY (`coverage_id`) REFERENCES `coverage` (`coverage_id`),
  CONSTRAINT `FK_BENEFIT_BENEFIT_ID` FOREIGN KEY (`benefit_id`) REFERENCES `benefit` (`benefit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `coverage_benefit` */

/*Table structure for table `domain_event_entry` */

DROP TABLE IF EXISTS `domain_event_entry`;

CREATE TABLE `domain_event_entry` (
  `aggregate_identifier` varchar(255) NOT NULL,
  `sequence_number` bigint(20) NOT NULL,
  `type` varchar(255) NOT NULL,
  `event_identifier` varchar(255) NOT NULL,
  `payload_revision` varchar(255) DEFAULT NULL,
  `payload_type` varchar(255) NOT NULL,
  `time_stamp` varchar(255) NOT NULL,
  `meta_data` longblob,
  `payload` longblob NOT NULL,
  PRIMARY KEY (`aggregate_identifier`,`sequence_number`,`type`),
  UNIQUE KEY `UK_fwe6lsa8bfo6hyas6ud3m8c7x` (`event_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `domain_event_entry` */

/*Table structure for table `geo` */

DROP TABLE IF EXISTS `geo`;

CREATE TABLE `geo` (
  `GEO_ID` varchar(20) NOT NULL,
  `PARENT_GEO_ID` varchar(20) DEFAULT NULL,
  `GEO_TYPE` varchar(20) NOT NULL,
  `GEO_DESCRIPTION` varchar(100) NOT NULL,
  PRIMARY KEY (`GEO_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `geo` */

/*Table structure for table `hcp_service` */

DROP TABLE IF EXISTS `hcp_service`;

CREATE TABLE `hcp_service` (
  `HCP_SERVICE_CODE` varchar(20) NOT NULL DEFAULT '',
  `HCP_DESCRIPTION` varchar(50) NOT NULL,
  `HCP_TYPE` varchar(50) NOT NULL,
  `ADDRESS` varchar(200) DEFAULT NULL,
  `PROVINCE_GEO_ID` varchar(20) NOT NULL,
  `TOWN_GEO_ID` varchar(20) NOT NULL,
  `POSTAL_CODE_GEO_ID` varchar(20) DEFAULT NULL,
  `WORK_PHONE` bigint(13) DEFAULT NULL,
  `EMAIL_ADDRESS` varchar(320) DEFAULT NULL,
  PRIMARY KEY (`HCP_SERVICE_CODE`),
  KEY `PROVINCE_GEO_ID` (`PROVINCE_GEO_ID`),
  KEY `TOWN_GEO_ID` (`TOWN_GEO_ID`),
  CONSTRAINT `hcp_service_ibfk_1` FOREIGN KEY (`PROVINCE_GEO_ID`) REFERENCES `geo` (`GEO_ID`),
  CONSTRAINT `hcp_service_ibfk_2` FOREIGN KEY (`TOWN_GEO_ID`) REFERENCES `geo` (`GEO_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `hcp_service` */

/*Table structure for table `plan` */

DROP TABLE IF EXISTS `plan`;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `plan` */

/*Table structure for table `plan_coverage` */

DROP TABLE IF EXISTS `plan_coverage`;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `plan_coverage` */

/*Table structure for table `plan_coverage_benefit` */

DROP TABLE IF EXISTS `plan_coverage_benefit`;

CREATE TABLE `plan_coverage_benefit` (
  `plan_coverage_id` varchar(255) NOT NULL,
  `benefit_id` varchar(255) DEFAULT NULL,
  `benefit_limit` decimal(19,2) DEFAULT NULL,
  `coverage_benefit_type` int(11) DEFAULT NULL,
  `defined_per` int(11) DEFAULT NULL,
  `max_limit` decimal(19,2) DEFAULT NULL,
  KEY `FK_8jd3iuqc7x0yb17aovswhs92n` (`plan_coverage_id`),
  CONSTRAINT `FK_8jd3iuqc7x0yb17aovswhs92n` FOREIGN KEY (`plan_coverage_id`) REFERENCES `plan_coverage` (`coverage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `plan_coverage_benefit` */

/*Table structure for table `plan_payment` */

DROP TABLE IF EXISTS `plan_payment`;

CREATE TABLE `plan_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `premium_payment_term_type` int(11) NOT NULL,
  `premium_payment_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_jc8gdw8f50sukxl8ldr98p2cu` (`premium_payment_id`),
  CONSTRAINT `FK_jc8gdw8f50sukxl8ldr98p2cu` FOREIGN KEY (`premium_payment_id`) REFERENCES `premium_payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `plan_payment` */

/*Table structure for table `plan_payment_maturity_amounts` */

DROP TABLE IF EXISTS `plan_payment_maturity_amounts`;

CREATE TABLE `plan_payment_maturity_amounts` (
  `plan_payment_id` bigint(20) NOT NULL,
  `guaranteed_survival_benefit_amount` decimal(19,2) NOT NULL,
  `maturity_year` int(11) NOT NULL,
  KEY `FK_7qovxgs106k36s3y5pohdq3iw` (`plan_payment_id`),
  CONSTRAINT `FK_7qovxgs106k36s3y5pohdq3iw` FOREIGN KEY (`plan_payment_id`) REFERENCES `plan_payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `plan_payment_maturity_amounts` */

/*Table structure for table `policy_term` */

DROP TABLE IF EXISTS `policy_term`;

CREATE TABLE `policy_term` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `max_maturity_age` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `policy_term` */

/*Table structure for table `policy_term_valid_terms` */

DROP TABLE IF EXISTS `policy_term_valid_terms`;

CREATE TABLE `policy_term_valid_terms` (
  `policy_term_id` bigint(20) NOT NULL,
  `valid_terms` int(11) DEFAULT NULL,
  KEY `FK_lqp4y9lbtc3yec8t3altqk935` (`policy_term_id`),
  CONSTRAINT `FK_lqp4y9lbtc3yec8t3altqk935` FOREIGN KEY (`policy_term_id`) REFERENCES `policy_term` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `policy_term_valid_terms` */

/*Table structure for table `premium_payment` */

DROP TABLE IF EXISTS `premium_payment`;

CREATE TABLE `premium_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `payment_cut_off_age` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `premium_payment` */

/*Table structure for table `premium_payment_valid_terms` */

DROP TABLE IF EXISTS `premium_payment_valid_terms`;

CREATE TABLE `premium_payment_valid_terms` (
  `premium_payment_id` bigint(20) NOT NULL,
  `valid_terms` int(11) DEFAULT NULL,
  KEY `FK_i4nq1052vfgxnmn4umabuox8y` (`premium_payment_id`),
  CONSTRAINT `FK_i4nq1052vfgxnmn4umabuox8y` FOREIGN KEY (`premium_payment_id`) REFERENCES `premium_payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `premium_payment_valid_terms` */

/*Table structure for table `region` */

DROP TABLE IF EXISTS `region`;

CREATE TABLE `region` (
  `REGION_CODE` varchar(20) NOT NULL DEFAULT '',
  `REGION` varchar(12) NOT NULL,
  PRIMARY KEY (`REGION_CODE`),
  UNIQUE KEY `REGION` (`REGION`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `region` */

/*Table structure for table `saga_entry` */

DROP TABLE IF EXISTS `saga_entry`;

CREATE TABLE `saga_entry` (
  `saga_id` varchar(255) NOT NULL,
  `revision` varchar(255) DEFAULT NULL,
  `saga_type` varchar(255) DEFAULT NULL,
  `serialized_saga` longblob,
  PRIMARY KEY (`saga_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `saga_entry` */



/*Table structure for table `snapshot_event_entry` */

DROP TABLE IF EXISTS `snapshot_event_entry`;

CREATE TABLE `snapshot_event_entry` (
  `aggregate_identifier` varchar(255) NOT NULL,
  `sequence_number` bigint(20) NOT NULL,
  `type` varchar(255) NOT NULL,
  `event_identifier` varchar(255) NOT NULL,
  `payload_revision` varchar(255) DEFAULT NULL,
  `payload_type` varchar(255) NOT NULL,
  `time_stamp` varchar(255) NOT NULL,
  `meta_data` longblob,
  `payload` longblob NOT NULL,
  PRIMARY KEY (`aggregate_identifier`,`sequence_number`,`type`),
  UNIQUE KEY `UK_e1uucjseo68gopmnd0vgdl44h` (`event_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `snapshot_event_entry` */

/*Table structure for table `sum_assured` */

DROP TABLE IF EXISTS `sum_assured`;

CREATE TABLE `sum_assured` (
  `type` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sum_assured` */

DROP TABLE IF EXISTS `sum_insured_values`;

CREATE TABLE `sum_insured_values` (
  `sum_assured_id` bigint(20) NOT NULL,
  `sum_insured_values` decimal(19,2) DEFAULT NULL,
  KEY `FK_28ctxup91kp61te2ela8peqo0` (`sum_assured_id`),
  CONSTRAINT `FK_28ctxup91kp61te2ela8peqo0` FOREIGN KEY (`sum_assured_id`) REFERENCES `sum_assured` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `sum_assured_sum_insured_values` */

DROP TABLE IF EXISTS `sum_assured_sum_insured_values`;

CREATE TABLE `sum_assured_sum_insured_values` (
  `sum_assured_id` bigint(20) NOT NULL,
  `sum_insured_values` decimal(19,2) DEFAULT NULL,
  KEY `FK_8v9k7sydll2yrf86atgs5081u` (`sum_assured_id`),
  CONSTRAINT `FK_8v9k7sydll2yrf86atgs5081u` FOREIGN KEY (`sum_assured_id`) REFERENCES `sum_assured` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sum_assured_sum_insured_values` */

DROP TABLE IF EXISTS `branch`;
CREATE TABLE `branch`(
  `BRANCH_CODE` varchar(20) NOT NULL,
  `REGION_CODE` varchar(20) NOT NULL,
  `BRANCH` varchar(255) NOT NULL,
  `BRANCH_MANAGER` varchar(255) NOT NULL,
  `BRANCH_BDE` varchar(255) NOT NULL,
  PRIMARY KEY (`BRANCH_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `region`;
CREATE TABLE `region`(
  `REGION_CODE` varchar(20) NOT NULL,
  `REGION` varchar(12) NOT NULL,
  `REGIONAL_MANAGER` varchar(255) NOT NULL,
  PRIMARY KEY (`REGION_CODE`),
  UNIQUE KEY `REGION` (`REGION`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `team` */

DROP TABLE IF EXISTS `team`;

CREATE TABLE `team` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `team` */

/*Table structure for table `team_team_leader_fulfillment` */

DROP TABLE IF EXISTS `team_team_leader_fulfillment`;
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
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `team_team_leader_fulfillment` */

DROP TABLE IF EXISTS `entity_sequence`;
CREATE TABLE `entity_sequence`(
  `sequence_id` INT(11) NOT NULL,
  `sequence_name` text NOT NULL,
  `sequence_number` DECIMAL(20,0) NOT NULL,
  `sequence_prefix` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`sequence_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
