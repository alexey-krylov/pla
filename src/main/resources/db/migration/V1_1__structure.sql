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
  SCHED_NAME        VARCHAR(120) NOT NULL,
  JOB_NAME          VARCHAR(200) NOT NULL,
  JOB_GROUP         VARCHAR(200) NOT NULL,
  DESCRIPTION       VARCHAR(250) NULL,
  JOB_CLASS_NAME    VARCHAR(250) NOT NULL,
  IS_DURABLE        VARCHAR(1)   NOT NULL,
  IS_NONCONCURRENT  VARCHAR(1)   NOT NULL,
  IS_UPDATE_DATA    VARCHAR(1)   NOT NULL,
  REQUESTS_RECOVERY VARCHAR(1)   NOT NULL,
  JOB_DATA          BLOB         NULL,
  PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
(
  SCHED_NAME     VARCHAR(120) NOT NULL,
  TRIGGER_NAME   VARCHAR(200) NOT NULL,
  TRIGGER_GROUP  VARCHAR(200) NOT NULL,
  JOB_NAME       VARCHAR(200) NOT NULL,
  JOB_GROUP      VARCHAR(200) NOT NULL,
  DESCRIPTION    VARCHAR(250) NULL,
  NEXT_FIRE_TIME BIGINT(13)   NULL,
  PREV_FIRE_TIME BIGINT(13)   NULL,
  PRIORITY       INTEGER      NULL,
  TRIGGER_STATE  VARCHAR(16)  NOT NULL,
  TRIGGER_TYPE   VARCHAR(8)   NOT NULL,
  START_TIME     BIGINT(13)   NOT NULL,
  END_TIME       BIGINT(13)   NULL,
  CALENDAR_NAME  VARCHAR(200) NULL,
  MISFIRE_INSTR  SMALLINT(2)  NULL,
  JOB_DATA       BLOB         NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
  REFERENCES QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
(
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(200) NOT NULL,
  TRIGGER_GROUP   VARCHAR(200) NOT NULL,
  REPEAT_COUNT    BIGINT(7)    NOT NULL,
  REPEAT_INTERVAL BIGINT(12)   NOT NULL,
  TIMES_TRIGGERED BIGINT(10)   NOT NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
(
  SCHED_NAME      VARCHAR(120) NOT NULL,
  TRIGGER_NAME    VARCHAR(200) NOT NULL,
  TRIGGER_GROUP   VARCHAR(200) NOT NULL,
  CRON_EXPRESSION VARCHAR(200) NOT NULL,
  TIME_ZONE_ID    VARCHAR(80),
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
  SCHED_NAME    VARCHAR(120)   NOT NULL,
  TRIGGER_NAME  VARCHAR(200)   NOT NULL,
  TRIGGER_GROUP VARCHAR(200)   NOT NULL,
  STR_PROP_1    VARCHAR(512)   NULL,
  STR_PROP_2    VARCHAR(512)   NULL,
  STR_PROP_3    VARCHAR(512)   NULL,
  INT_PROP_1    INT            NULL,
  INT_PROP_2    INT            NULL,
  LONG_PROP_1   BIGINT         NULL,
  LONG_PROP_2   BIGINT         NULL,
  DEC_PROP_1    NUMERIC(13, 4) NULL,
  DEC_PROP_2    NUMERIC(13, 4) NULL,
  BOOL_PROP_1   VARCHAR(1)     NULL,
  BOOL_PROP_2   VARCHAR(1)     NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_NAME  VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  BLOB_DATA     BLOB         NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  CALENDAR_NAME VARCHAR(200) NOT NULL,
  CALENDAR      BLOB         NOT NULL,
  PRIMARY KEY (SCHED_NAME, CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
(
  SCHED_NAME    VARCHAR(120) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  PRIMARY KEY (SCHED_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  ENTRY_ID          VARCHAR(95)  NOT NULL,
  TRIGGER_NAME      VARCHAR(200) NOT NULL,
  TRIGGER_GROUP     VARCHAR(200) NOT NULL,
  INSTANCE_NAME     VARCHAR(200) NOT NULL,
  FIRED_TIME        BIGINT(13)   NOT NULL,
  SCHED_TIME        BIGINT(13)   NOT NULL,
  PRIORITY          INTEGER      NOT NULL,
  STATE             VARCHAR(16)  NOT NULL,
  JOB_NAME          VARCHAR(200) NULL,
  JOB_GROUP         VARCHAR(200) NULL,
  IS_NONCONCURRENT  VARCHAR(1)   NULL,
  REQUESTS_RECOVERY VARCHAR(1)   NULL,
  PRIMARY KEY (SCHED_NAME, ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
(
  SCHED_NAME        VARCHAR(120) NOT NULL,
  INSTANCE_NAME     VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT(13)   NOT NULL,
  CHECKIN_INTERVAL  BIGINT(13)   NOT NULL,
  PRIMARY KEY (SCHED_NAME, INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  LOCK_NAME  VARCHAR(40)  NOT NULL,
  PRIMARY KEY (SCHED_NAME, LOCK_NAME)
);
COMMIT;

USE PLA;

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE = '' */;

/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

/*Table structure for table `agent` */

DROP TABLE IF EXISTS `agent`;

CREATE TABLE `agent` (
  `agent_id`                       INT(11) NOT NULL,
  `code`                           VARCHAR(255) DEFAULT NULL,
  `description`                    VARCHAR(255) DEFAULT NULL,
  `employee_id`                    VARCHAR(255) DEFAULT NULL,
  `first_name`                     VARCHAR(255) DEFAULT NULL,
  `last_name`                      VARCHAR(255) DEFAULT NULL,
  `nrc_number`                     INT(11)      DEFAULT NULL,
  `title`                          VARCHAR(255) DEFAULT NULL,
  `training_complete_on`           DATE         DEFAULT NULL,
  `agent_status`                   VARCHAR(255) DEFAULT NULL,
  `address_line1`                  VARCHAR(255) DEFAULT NULL,
  `address_line2`                  VARCHAR(255) DEFAULT NULL,
  `email`                          VARCHAR(255) DEFAULT NULL,
  `city`                           VARCHAR(255) DEFAULT NULL,
  `postal_code`                    INT(11) NOT NULL,
  `province`                       VARCHAR(255) DEFAULT NULL,
  `home_phone_number`              INT(11) NOT NULL,
  `mobile_number`                  INT(11) NOT NULL,
  `work_phone_number`              INT(11) NOT NULL,
  `license_number`                 VARCHAR(255) DEFAULT NULL,
  `override_commission_applicable` VARCHAR(255) DEFAULT NULL,
  `physical_address_line1`         VARCHAR(255) DEFAULT NULL,
  `physical_address_line2`         VARCHAR(255) DEFAULT NULL,
  `physical_address_city`          VARCHAR(255) DEFAULT NULL,
  `physical_address_postal_code`   INT(11)      DEFAULT NULL,
  `physical_address_province`      VARCHAR(255) DEFAULT NULL,
  `team_id`                        VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`agent_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `agent` */

/*Table structure for table `agent_authorized_plan` */

DROP TABLE IF EXISTS `agent_authorized_plan`;

CREATE TABLE `agent_authorized_plan` (
  `agent_id` INT(11) NOT NULL,
  `plan_id`  VARCHAR(255) DEFAULT NULL,
  KEY `FK_AGENT_PLAN_ID` (`agent_id`),
  CONSTRAINT `FK_AUTH_PLAN` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`agent_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `agent_authorized_plan` */

/*Table structure for table `association_value_entry` */

DROP TABLE IF EXISTS `association_value_entry`;

CREATE TABLE `association_value_entry` (
  `id`                BIGINT(20) NOT NULL AUTO_INCREMENT,
  `association_key`   VARCHAR(255)        DEFAULT NULL,
  `association_value` VARCHAR(255)        DEFAULT NULL,
  `saga_id`           VARCHAR(255)        DEFAULT NULL,
  `saga_type`         VARCHAR(255)        DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `association_value_entry` */

/*Table structure for table `bank_branch` */

DROP TABLE IF EXISTS `bank_branch`;

CREATE TABLE `bank_branch` (
  `BANK_CODE` VARCHAR(20)  NOT NULL,
  `BRANCH`    VARCHAR(100) NOT NULL,
  `SORT_CODE` VARCHAR(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`SORT_CODE`),
  KEY `BANK_CODE` (`BANK_CODE`),
  CONSTRAINT `bank_branch_ibfk_1` FOREIGN KEY (`BANK_CODE`) REFERENCES `bank_name` (`BANK_CODE`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `bank_branch` */

/*Table structure for table `bank_name` */

DROP TABLE IF EXISTS `bank_name`;

CREATE TABLE `bank_name` (
  `BANK_CODE` VARCHAR(20) NOT NULL DEFAULT '',
  `BANK_NAME` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`BANK_CODE`),
  UNIQUE KEY `BANK_NAME` (`BANK_NAME`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `bank_name` */

/*Table structure for table `benefit` */

DROP TABLE IF EXISTS `benefit`;
CREATE TABLE `benefit` (
  `benefit_id`   VARCHAR(255) NOT NULL,
  `status`       VARCHAR(255) NOT NULL,
  `benefit_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`benefit_id`),
  UNIQUE KEY `UNQ_BENEFIT_NAME` (`benefit_name`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `benefit` */

/*Table structure for table `channel_type` */

DROP TABLE IF EXISTS `channel_type`;

CREATE TABLE `channel_type` (
  `CHANNEL_CODE`        VARCHAR(20) NOT NULL DEFAULT '',
  `CHANNEL_DESCRIPTION` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`CHANNEL_CODE`),
  UNIQUE KEY `CHANNEL_DESCRIPTION` (`CHANNEL_DESCRIPTION`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `channel_type` */

/*Table structure for table `coverage` */

DROP TABLE IF EXISTS `coverage`;

CREATE TABLE `coverage` (
  `coverage_id`   VARCHAR(255) NOT NULL,
  `coverage_name` VARCHAR(50)  NOT NULL,
  `description`   VARCHAR(150) DEFAULT NULL,
  `status`        VARCHAR(255) NOT NULL,
  PRIMARY KEY (`coverage_id`),
  UNIQUE KEY `UNQ_COVERAGE_NAME` (`coverage_name`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `coverage` */

/*Table structure for table `coverage_benefit` */

DROP TABLE IF EXISTS `coverage_benefit`;

CREATE TABLE `coverage_benefit` (
  `coverage_id` VARCHAR(255) NOT NULL,
  `benefit_id`  VARCHAR(255) NOT NULL,
  UNIQUE KEY `UK_BENEFIT_ID` (`benefit_id`),
  KEY `FK_COVERAGE_ID` (`coverage_id`),
  CONSTRAINT `FK_COVERAGE_COVERAGE_ID` FOREIGN KEY (`coverage_id`) REFERENCES `coverage` (`coverage_id`),
  CONSTRAINT `FK_BENEFIT_BENEFIT_ID` FOREIGN KEY (`benefit_id`) REFERENCES `benefit` (`benefit_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `coverage_benefit` */

/*Table structure for table `domain_event_entry` */

DROP TABLE IF EXISTS `domain_event_entry`;

CREATE TABLE `domain_event_entry` (
  `aggregate_identifier` VARCHAR(255) NOT NULL,
  `sequence_number`      BIGINT(20)   NOT NULL,
  `type`                 VARCHAR(255) NOT NULL,
  `event_identifier`     VARCHAR(255) NOT NULL,
  `payload_revision`     VARCHAR(255) DEFAULT NULL,
  `payload_type`         VARCHAR(255) NOT NULL,
  `time_stamp`           VARCHAR(255) NOT NULL,
  `meta_data`            LONGBLOB,
  `payload`              LONGBLOB     NOT NULL,
  PRIMARY KEY (`aggregate_identifier`, `sequence_number`, `type`),
  UNIQUE KEY `UK_fwe6lsa8bfo6hyas6ud3m8c7x` (`event_identifier`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `domain_event_entry` */

/*Table structure for table `geo` */

DROP TABLE IF EXISTS `geo`;

CREATE TABLE `geo` (
  `GEO_ID`          VARCHAR(20)  NOT NULL,
  `PARENT_GEO_ID`   VARCHAR(20) DEFAULT NULL,
  `GEO_TYPE`        VARCHAR(20)  NOT NULL,
  `GEO_DESCRIPTION` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`GEO_ID`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `geo` */

/*Table structure for table `hcp_service` */

DROP TABLE IF EXISTS `hcp_service`;

CREATE TABLE `hcp_service` (
  `HCP_SERVICE_CODE`   VARCHAR(20) NOT NULL DEFAULT '',
  `HCP_DESCRIPTION`    VARCHAR(50) NOT NULL,
  `HCP_TYPE`           VARCHAR(50) NOT NULL,
  `ADDRESS`            VARCHAR(200)         DEFAULT NULL,
  `PROVINCE_GEO_ID`    VARCHAR(20) NOT NULL,
  `TOWN_GEO_ID`        VARCHAR(20) NOT NULL,
  `POSTAL_CODE_GEO_ID` VARCHAR(20)          DEFAULT NULL,
  `WORK_PHONE`         BIGINT(13)           DEFAULT NULL,
  `EMAIL_ADDRESS`      VARCHAR(320)         DEFAULT NULL,
  PRIMARY KEY (`HCP_SERVICE_CODE`),
  KEY `PROVINCE_GEO_ID` (`PROVINCE_GEO_ID`),
  KEY `TOWN_GEO_ID` (`TOWN_GEO_ID`),
  CONSTRAINT `hcp_service_ibfk_1` FOREIGN KEY (`PROVINCE_GEO_ID`) REFERENCES `geo` (`GEO_ID`),
  CONSTRAINT `hcp_service_ibfk_2` FOREIGN KEY (`TOWN_GEO_ID`) REFERENCES `geo` (`GEO_ID`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `hcp_service` */

DROP TABLE IF EXISTS `plan_entry`;

CREATE TABLE `plan_entry` (
  `plan_id`             VARCHAR(255) NOT NULL,
  `client_type`         INT(11)      DEFAULT NULL,
  `launch_date`         DATE         DEFAULT NULL,
  `line_of_business_id` VARCHAR(255) DEFAULT NULL,
  `plan_code`           VARCHAR(255) DEFAULT NULL,
  `plan_name`           VARCHAR(255) DEFAULT NULL,
  `plan_type`           INT(11)      DEFAULT NULL,
  `withdrawal_date`     DATE         DEFAULT NULL,
  PRIMARY KEY (`plan_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `plan_relationship`;

CREATE TABLE `plan_relationship` (
  `plan_id`                  VARCHAR(255) NOT NULL,
  `applicable_relationships` INT(11) DEFAULT NULL,
  KEY `FK_luppdjc65purrwcxtvryt5jdc` (`plan_id`),
  CONSTRAINT `FK_luppdjc65purrwcxtvryt5jdc` FOREIGN KEY (`plan_id`) REFERENCES `plan_entry` (`plan_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `plan_term`;

CREATE TABLE `plan_term` (
  `plan_id`       VARCHAR(255) NOT NULL,
  `premium_term`  INT(11) DEFAULT NULL,
  `policy_term`   INT(11) DEFAULT NULL,
  `coverage_term` INT(11) DEFAULT NULL,
  KEY `FK_7b1yq3vsok12wpdsyke56hskf` (`plan_id`),
  CONSTRAINT `FK_7b1yq3vsok12wpdsyke56hskf` FOREIGN KEY (`plan_id`) REFERENCES `plan_entry` (`plan_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `plan_coverage`;

CREATE TABLE `plan_coverage` (
  `coverage_id` varchar(255) NOT NULL,
  `plan_id`          VARCHAR(255) NOT NULL,
  `MAX_SUM_ASSURED`  DECIMAL(19, 2) DEFAULT NULL,
  `MIN_SUM_ASSURED`  DECIMAL(19, 2) DEFAULT NULL,
  `MULTIPLES_OF`     INT(11)      NOT NULL,
  `SUM_ASSURED_TYPE` VARCHAR(255)   DEFAULT NULL,
  PRIMARY KEY (`coverage_id`, `plan_id`),
  KEY `FK_PC_PLAN_ID` (`plan_id`),
  CONSTRAINT `FK_PC_PLAN_ID` FOREIGN KEY (`plan_id`) REFERENCES `plan_entry` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `plan_coverage_sum_assured` (
  `plan_id`     VARCHAR(255) NOT NULL,
  `coverage_id` VARCHAR(255) NOT NULL,
  `SUM_ASSURED` DECIMAL(19, 2) DEFAULT NULL,
  KEY `FK_loxoastuo296cmje96fi164ru` (`plan_id`, `coverage_id`),
  CONSTRAINT `FK_loxoastuo296cmje96fi164ru` FOREIGN KEY (`plan_id`, `coverage_id`) REFERENCES `plan_coverage` (`coverage_id`, `plan_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Table structure for table `region` */

DROP TABLE IF EXISTS `region`;

CREATE TABLE `region` (
  `REGION_CODE` VARCHAR(20) NOT NULL DEFAULT '',
  `REGION`      VARCHAR(12) NOT NULL,
  PRIMARY KEY (`REGION_CODE`),
  UNIQUE KEY `REGION` (`REGION`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `region` */

/*Table structure for table `saga_entry` */

DROP TABLE IF EXISTS `saga_entry`;

CREATE TABLE `saga_entry` (
  `saga_id`         VARCHAR(255) NOT NULL,
  `revision`        VARCHAR(255) DEFAULT NULL,
  `saga_type`       VARCHAR(255) DEFAULT NULL,
  `serialized_saga` LONGBLOB,
  PRIMARY KEY (`saga_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `saga_entry` */



/*Table structure for table `snapshot_event_entry` */

DROP TABLE IF EXISTS `snapshot_event_entry`;

CREATE TABLE `snapshot_event_entry` (
  `aggregate_identifier` VARCHAR(255) NOT NULL,
  `sequence_number`      BIGINT(20)   NOT NULL,
  `type`                 VARCHAR(255) NOT NULL,
  `event_identifier`     VARCHAR(255) NOT NULL,
  `payload_revision`     VARCHAR(255) DEFAULT NULL,
  `payload_type`         VARCHAR(255) NOT NULL,
  `time_stamp`           VARCHAR(255) NOT NULL,
  `meta_data`            LONGBLOB,
  `payload`              LONGBLOB     NOT NULL,
  PRIMARY KEY (`aggregate_identifier`, `sequence_number`, `type`),
  UNIQUE KEY `UK_e1uucjseo68gopmnd0vgdl44h` (`event_identifier`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `snapshot_event_entry` */

DROP TABLE IF EXISTS `branch`;
CREATE TABLE `branch` (
  `BRANCH_CODE`    VARCHAR(20)  NOT NULL,
  `REGION_CODE`    VARCHAR(20)  NOT NULL,
  `BRANCH`         VARCHAR(255) NOT NULL,
  `BRANCH_MANAGER` VARCHAR(255) NOT NULL,
  `BRANCH_BDE`     VARCHAR(255) NOT NULL,
  PRIMARY KEY (`BRANCH_CODE`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `region`;
CREATE TABLE `region` (
  `REGION_CODE`      VARCHAR(20)  NOT NULL,
  `REGION`           VARCHAR(12)  NOT NULL,
  `REGIONAL_MANAGER` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`REGION_CODE`),
  UNIQUE KEY `REGION` (`REGION`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Table structure for table `team` */

DROP TABLE IF EXISTS `team`;

CREATE TABLE `team` (
  `team_id`             VARCHAR(255) NOT NULL,
  `active`              BIT(1)       DEFAULT NULL,
  `current_team_leader` VARCHAR(255) DEFAULT NULL,
  `team_code`           VARCHAR(255) DEFAULT NULL,
  `team_name`           VARCHAR(255) DEFAULT NULL,
  `region_code`         VARCHAR(20)  NOT NULL,
  `branch_code`         VARCHAR(20)  NOT NULL,
  PRIMARY KEY (`team_id`),
  UNIQUE KEY `UNQ_TEAM_CODE_NAME` (`team_code`, `team_name`),
  KEY `FK_TEAM_REGION_REGION_CODE` (`region_code`),
  KEY `FK_TEAM_BRANCH_BRANCH_CODE` (`branch_code`),
  CONSTRAINT `FK_TEAM_BRANCH_BRANCH_CODE` FOREIGN KEY (`branch_code`) REFERENCES `branch` (`BRANCH_CODE`),
  CONSTRAINT `FK_TEAM_REGION_REGION_CODE` FOREIGN KEY (`region_code`) REFERENCES `region` (`REGION_CODE`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `team` */

/*Table structure for table `team_team_leader_fulfillment` */

DROP TABLE IF EXISTS `team_team_leader_fulfillment`;
CREATE TABLE `team_team_leader_fulfillment` (
  `team_id`            VARCHAR(255) NOT NULL,
  `from_date`          DATE                  DEFAULT NULL,
  `employee_id`        VARCHAR(255)          DEFAULT NULL,
  `first_name`         VARCHAR(255)          DEFAULT NULL,
  `last_name`          VARCHAR(255)          DEFAULT NULL,
  `thru_date`          DATE                  DEFAULT NULL,
  `team_leaders_order` INT(11)      NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`team_id`, `team_leaders_order`),
  KEY `team_leaders_order` (`team_leaders_order`),
  CONSTRAINT `FK_TEAM_LEADER_FULFILLMENT` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*Data for the table `team_team_leader_fulfillment` */

DROP TABLE IF EXISTS `entity_sequence`;
CREATE TABLE `entity_sequence` (
  `sequence_id`     INT(11)        NOT NULL,
  `sequence_name`   VARCHAR(20)    NOT NULL,
  `sequence_number` DECIMAL(20, 0) NOT NULL,
  `sequence_prefix` VARCHAR(255)   NOT NULL,
  PRIMARY KEY (`sequence_id`)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;
