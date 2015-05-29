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

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

/*Table structure for table `agent` */
DROP TABLE IF EXISTS `agent`;
CREATE TABLE `agent` (
  `agent_id`                       VARCHAR(255) NOT NULL,
  `designation_code`               VARCHAR(255) DEFAULT NULL,
  `designation_name`               VARCHAR(255) DEFAULT NULL,
  `employee_id`                    VARCHAR(255) DEFAULT NULL,
  `first_name`                     VARCHAR(255) DEFAULT NULL,
  `last_name`                      VARCHAR(255) DEFAULT NULL,
  `nrc_number`                     INT(11)      DEFAULT NULL,
  `title`                          VARCHAR(255) DEFAULT NULL,
  `training_complete_on`           DATE         DEFAULT NULL,
  `agent_status`                   VARCHAR(255) DEFAULT NULL,
  `channel_code`                   VARCHAR(255) DEFAULT NULL,
  `channel_name`                   VARCHAR(255) DEFAULT NULL,
  `address_line1`                  VARCHAR(255) DEFAULT NULL,
  `address_line2`                  VARCHAR(255) DEFAULT NULL,
  `email`                          VARCHAR(255) DEFAULT NULL,
  `city`                           VARCHAR(255) DEFAULT NULL,
  `postal_code`                    INT(11)      DEFAULT NULL,
  `province`                       VARCHAR(255) DEFAULT NULL,
  `home_phone_number`              VARCHAR(255) DEFAULT NULL,
  `mobile_number`                  VARCHAR(255) DEFAULT NULL,
  `work_phone_number`              VARCHAR(255) DEFAULT NULL,
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
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

DROP TABLE IF EXISTS `agent_authorized_plan`;
CREATE TABLE `agent_authorized_plan` (
  `agent_id` VARCHAR(255) NOT NULL,
  `plan_id`  VARCHAR(255) DEFAULT NULL,
  KEY `FK_fb90q6b5wf5iswifmkro1bfr8` (`agent_id`),
  CONSTRAINT `FK_fb90q6b5wf5iswifmkro1bfr8` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`agent_id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;


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
  PRIMARY KEY (`benefit_id`)
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
  `coverage_code` varchar(255) NOT NULL,
  `description` varchar(150) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`coverage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `coverage` */

/*Table structure for table `coverage_benefit` */

DROP TABLE IF EXISTS `coverage_benefit`;

CREATE TABLE `coverage_benefit` (
  `coverage_id` varchar(255) NOT NULL,
  `benefit_id` varchar(255) NOT NULL,
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


/*Data for the table `premium_payment_valid_terms` */

/*Table structure for table `region` */


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


DROP TABLE IF EXISTS region;
CREATE TABLE `region` (
  `region_code`      VARCHAR(255) NOT NULL,
  `region_name`      VARCHAR(255) DEFAULT NULL,
  `regional_manager` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`region_code`),
  UNIQUE KEY `UNQ_REGION_CODE_NAME` (`region_code`, `region_name`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

DROP TABLE IF EXISTS branch;

DROP TABLE IF EXISTS `branch`;
CREATE TABLE `branch` (
  `branch_code`            VARCHAR(255) NOT NULL,
  `current_branch_bde`     VARCHAR(255) DEFAULT NULL,
  `current_branch_manager` VARCHAR(255) DEFAULT NULL,
  `branch_name`            VARCHAR(255) NOT NULL,
  PRIMARY KEY (`branch_code`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

DROP TABLE IF EXISTS `region_branch`;
CREATE TABLE `region_branch` (
  `region_code` VARCHAR(255) NOT NULL,
  `branch_code` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`region_code`, `branch_code`),
  UNIQUE KEY `UK_n48d3jv1a5x3x5wh2wqv6sebr` (`branch_code`),
  CONSTRAINT `FK_1knyc3q94ravrw1qwm7xnx73u` FOREIGN KEY (`region_code`) REFERENCES `region` (`region_code`),
  CONSTRAINT `FK_n48d3jv1a5x3x5wh2wqv6sebr` FOREIGN KEY (`branch_code`) REFERENCES `branch` (`branch_code`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*Data for the table `sum_assured_sum_insured_values` */


/*Table structure for table `team` */

DROP TABLE IF EXISTS `team`;

CREATE TABLE `team` (
  `team_id` varchar(255) NOT NULL,
  `active` bit(1) DEFAULT NULL,
  `branch_code` varchar(255) DEFAULT NULL,
  `current_team_leader` varchar(255) DEFAULT NULL,
  `region_code` varchar(255) DEFAULT NULL,
  `team_code` varchar(255) DEFAULT NULL,
  `team_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`team_id`),
  UNIQUE KEY `UNQ_ACTIVE_TEAM_CODE_NAME` (`team_code`, `team_name`, `active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Data for the table `team` */

/*Table structure for table `team_team_leader_fulfillment` */
DROP TABLE IF EXISTS `team_team_leader_fulfillment`;
CREATE TABLE `team_team_leader_fulfillment` (
  `team_id` varchar(255) NOT NULL,
  `from_date` date DEFAULT NULL,
  `employee_id` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `thru_date` date DEFAULT NULL,
  KEY `FK_71twwvq2jttbb9jfw9xom6y08` (`team_id`),
  CONSTRAINT `FK_71twwvq2jttbb9jfw9xom6y08` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `team_team_leader_fulfillment` */

DROP TABLE IF EXISTS region_manager_fulfillment;
CREATE TABLE `region_manager_fulfillment` (
  `region_code` varchar(255) NOT NULL,
  `first_name` VARCHAR(255) NOT NULL,
  `last_name`  VARCHAR(255) NOT NULL,
  `from_date`  DATE DEFAULT NULL,
  `thru_date`  DATE DEFAULT NULL,
  `employee_id` varchar(255) NULL,
  CONSTRAINT `FK_REGION_CODE_REGION_MANAGER_FULFILLMENT_REGION_CODE` FOREIGN KEY (`region_Code`) REFERENCES `region` (`region_code`)
) ENGINE =InnoDB  DEFAULT CHARSET =utf8;

DROP TABLE IF EXISTS branch_manager_fulfillment;
CREATE TABLE `branch_manager_fulfillment` (
  `branch_code` VARCHAR(255) NOT NULL,
  `first_name`  VARCHAR(255) NOT NULL,
  `last_name`   VARCHAR(255) NOT NULL,
  `from_date`   DATE         NOT NULL,
  `thru_date`   DATE DEFAULT NULL,
  `employee_id` VARCHAR(255) NOT NULL,
  CONSTRAINT `FK_BRANCH_CODE_BRANCH_FULFILLMENT` FOREIGN KEY (`branch_code`) REFERENCES `branch` (`branch_code`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

DROP TABLE IF EXISTS branch_bde_fulfillment;
CREATE TABLE `branch_bde_fulfillment` (
  `branch_code` varchar(255) NOT NULL,
  `first_name`  varchar(255) NOT NULL,
  `last_name`   varchar(255) NOT NULL,
  `from_date`   date DEFAULT NULL,
  `thru_date`   date DEFAULT NULL,
  `employee_id` varchar(255) NOT NULL,
  CONSTRAINT `FK_BRANCH_CODE_BRANCH_BDE_FULFILLMENT_CODE` FOREIGN KEY (`branch_code`) REFERENCES `branch` (`branch_code`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

DROP TABLE IF EXISTS `entity_sequence`;
CREATE TABLE `entity_sequence` (
  `sequence_id` int(11) NOT NULL,
  `sequence_name` text NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `sequence_prefix` varchar(255) NOT NULL,
  PRIMARY KEY (`sequence_id`)
)ENGINE =InnoDB DEFAULT CHARSET =utf8;

DROP TABLE IF EXISTS `commission`;
CREATE TABLE `commission` (
  `commission_id`   VARCHAR(255) NOT NULL,
  `available_for`   VARCHAR(255) DEFAULT NULL,
  `commission_type` VARCHAR(255) DEFAULT NULL,
  `from_date`       DATE         DEFAULT NULL,
  `plan_id`         VARCHAR(255) DEFAULT NULL,
  `premium_fee`     VARCHAR(255) DEFAULT NULL,
  `thru_date`       DATE         DEFAULT NULL,
  PRIMARY KEY (`commission_id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

DROP TABLE IF EXISTS `commission_commission_term`;
CREATE TABLE `commission_commission_term` (
  `commission_id`         VARCHAR(255) NOT NULL,
  `commission_percentage` DECIMAL(19, 2) DEFAULT NULL,
  `commission_term_type` VARCHAR(255) DEFAULT NULL,
  `end_year`              INT(11)        DEFAULT NULL,
  `start_year`            INT(11)        DEFAULT NULL,
  KEY `FK_as28e68p5ow4r4rrxui4kx64l` (`commission_id`),
  CONSTRAINT `FK_as28e68p5ow4r4rrxui4kx64l` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`commission_id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;


DROP VIEW IF EXISTS `agent_team_branch_view`;
CREATE  VIEW `agent_team_branch_view` AS
  (SELECT DISTINCT
     `agent_id`                                                             AS agentId,
     A.title                                                                AS title,
     `designation_code`                                                     AS designationCode,
     `designation_name`                                                     AS designationName,
     A.employee_id                                                          AS employeeId,
     A.first_name                                                           AS firstName,
     A.`last_name`                                                          AS lastName,
     `nrc_number`                                                           AS nrcNumber,
     `training_complete_on`                                                 AS trainingCompletedOn,
     agent_status                                                           AS agentStatus,
     channel_code                                                           AS channelCode,
     channel_name                                                           AS channelName,
     address_line1                                                          AS addressLine1,
     address_line2                                                          AS addressLine2,
     email,
     A.city                                                                 AS cityCode,
     (SELECT geo_description
      FROM geo
      WHERE geo_type = 'CITY' AND geo_id = A.city)                          AS cityName,
     postal_code                                                            AS postalCode,
     A.province                                                             AS provinceCode,
     (SELECT geo_description
      FROM geo
      WHERE geo_type = 'PROVINCE' AND geo_id = A.province)                  AS provinceName,
     home_phone_number                                                      AS homePhoneNumber,
     mobile_number                                                          AS mobileNumber,
     work_phone_number                                                      AS workPhoneNumber,
     license_number                                                         AS licenseNumber,
     `override_commission_applicable`                                       AS overrideCommissionApplicable,
     physical_address_line1                                                 AS physicalAddressLine1,
     physical_address_line2                                                 AS physicalAddressLine2,
     physical_address_city                                                  AS physicalAddressCityCode,
     (SELECT geo_description
      FROM geo
      WHERE geo_type = 'CITY' AND geo_id = A.physical_address_city)         AS physicalAddressCityName,
     physical_address_postal_code                                           AS physicalAddressPostalCode,
     physical_address_province                                              AS physicalAddressProvinceCode,
     (SELECT geo_description
      FROM geo
      WHERE geo_type = 'PROVINCE' AND geo_id = A.physical_address_province) AS physicalAddressProvinceName,
     A.team_id                                                              AS teamId,
     TF.employee_id                                                         AS teamLeaderId,
     TF.first_name                                                          AS teamLeaderFirstName,
     TF.last_name                                                           AS teamLeaderLastName,
     T.team_code                                                            AS teamCode,
     T.team_name                                                            AS teamName,
     R.region_code                                                          AS regionCode,
     R.region_name                                                          AS regionName,
     B.branch_code                                                          AS branchCode,
     B.branch_name                                                          AS branchName
   FROM  agent A  LEFT JOIN team T    ON A.team_id = T.`team_id`  LEFT JOIN `team_team_leader_fulfillment` TF    ON T.`team_id` = TF.`team_id`
                                                                                                                    AND
                                                                                                                    CASE WHEN TF.thru_date IS NULL THEN T.current_team_leader=TF.employee_id
                                                                                                                    WHEN DATE_ADD(TF.thru_date, INTERVAL 1 DAY) > NOW() THEN T.`team_id` = TF.`team_id`
                                                                                                                    END

     LEFT JOIN region R ON T.region_code = R.REGION_CODE
     LEFT JOIN branch B ON T.branch_code = B.branch_code GROUP BY A.agent_id
     ORDER BY TF.from_date DESC);

DROP VIEW IF EXISTS `region_region_manger_fulfilment_view`;
CREATE VIEW `region_region_manger_fulfilment_view` AS
  (SELECT
     R.region_code AS regionCode,
     R.region_name AS regionName,
     RF.first_name AS regionalManagerFirstName,
     RF.last_name  AS regionalManagerLastName,
     RF.from_date   AS regionalManagerFromDate,
     RF.employee_id AS currentRegionalManager
   FROM region R LEFT JOIN `region_manager_fulfillment` RF
       ON R.region_code = RF.region_code AND R.regional_manager = RF.employee_id AND RF.thru_date IS NULL);

DROP VIEW IF EXISTS `region_region_manger_fulfilment_greater_than_current_date_view`;
CREATE VIEW `region_region_manger_fulfilment_greater_than_current_date_view` AS
  (SELECT
     R.region_code  AS regionCode,
     R.region_name  AS regionName,
     RF.first_name  AS regionalManagerFirstName,
     RF.last_name   AS regionalManagerLastName,
     RF.from_date   AS regionalManagerFromDate,
     RF.thru_date   AS regionalManagerThruDate,
     RF.employee_id AS currentRegionalManager
   FROM region R LEFT JOIN `region_manager_fulfillment` RF
       ON R.region_code = RF.region_code
          /* AND R.regional_manager = RF.employee_id */
          AND ((RF.thru_date IS NULL) OR (RF.thru_date >= CURDATE())));

DROP VIEW IF EXISTS `active_team_region_branch_view`;
CREATE VIEW `active_team_region_branch_view` AS
  (SELECT
     tm.team_id             AS teamId,
     tm.team_name           AS teamName,
     tm.team_code           AS teamCode,
     tm.current_team_leader AS currentTeamLeader,
     tf.first_Name          AS firstName,
     tf.last_Name           AS lastName,
     tf.from_date           AS fromDate,
     tf.thru_date           AS endDate,
     b.branch_name          AS branchName,
     r.region_name          AS regionName,
     b.branch_code          AS branchCode,
     r.region_code          AS regionCode
   FROM team tm
     INNER JOIN team_team_leader_fulfillment tf
       ON tm.`team_id` = TF.`team_id`
          AND
          CASE WHEN TF.thru_date IS NULL THEN tm.current_team_leader=TF.employee_id
          WHEN DATE_ADD(TF.thru_date, INTERVAL 1 DAY) > NOW() THEN tm.`team_id` = TF.`team_id`
          END
     INNER JOIN region r ON tm.region_code = r.region_code
     INNER JOIN branch b ON tm.branch_code = b.branch_code
   WHERE tm.active = '1' GROUP BY tm.team_id
   ORDER BY TF.from_date DESC);

DROP VIEW IF EXISTS `active_team_team_fulfillment_greater_than_current_date`;
CREATE VIEW `active_team_team_fulfillment_greater_than_current_date` AS
  (SELECT
     tm.team_id             AS teamId,
     tm.team_name           AS teamName,
     tm.team_code           AS teamCode,
     tm.current_team_leader AS currentTeamLeader,
     tf.first_Name          AS firstName,
     tf.last_Name           AS lastName,
     tf.from_date           AS fromDate,
     tf.thru_date           AS endDate,
     b.branch_name          AS branchName,
     r.region_name          AS regionName,
     b.branch_code          AS branchCode,
     r.region_code          AS regionCode
   FROM team tm
     INNER JOIN team_team_leader_fulfillment tf
       ON tf.team_id = tm.team_id AND
          /* tm.current_team_leader = tf.employee_id AND */
          ((tf.thru_date IS NULL) OR (tf.thru_date >= CURDATE()))
     INNER JOIN region r ON tm.region_code = r.region_code
     INNER JOIN branch b ON tm.branch_code = b.branch_code
   WHERE tm.active = '1');

DROP VIEW IF EXISTS `commission_view`;
CREATE VIEW `commission_view` AS
  (SELECT
     cm.commission_id        AS commissionId,
     cm.from_date            AS fromDate,
     cm.thru_date            AS toDate,
     cm.plan_id              AS planId,
     cm.available_for        AS availableFor,
     cm.commission_type AS commissionType,
     cm.premium_fee     AS premiumFee
   FROM commission cm);

DROP VIEW IF EXISTS `commission_commission_term_view`;
CREATE VIEW `commission_commission_term_view` AS
  (SELECT
     cctf.commission_id         AS commissionId,
     cctf.commission_percentage AS commissionPercentage,
     cctf.start_year            AS startYear,
     cctf.end_year             AS endYear,
     cctf.commission_term_type AS commissionTermType
   FROM COMMISSION_COMMISSION_TERM cctf);


/*Table structure for mandatory_document */

DROP TABLE IF EXISTS `mandatory_document`;
CREATE TABLE `mandatory_document` (
  `document_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `coverage_id` varchar(255) DEFAULT NULL,
  `plan_id` varchar(255) DEFAULT NULL,
  `process` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`document_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


/*Table structure for mandatory_documents */

DROP TABLE IF EXISTS `mandatory_documents`;
CREATE TABLE `mandatory_documents` (
  `document_id` bigint(20) NOT NULL,
  `document_code` varchar(255) DEFAULT NULL,
  KEY `FK_tc6y6qyosoy6f2xjh3i6kv65o` (`document_id`),
  CONSTRAINT `FK_tc6y6qyosoy6f2xjh3i6kv65o` FOREIGN KEY (`document_id`) REFERENCES `mandatory_document` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for document */


DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
  `document_code` varchar(255) NOT NULL,
  `document_name` varchar(255) DEFAULT NULL,
  `is_provided` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`document_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP VIEW IF EXISTS `document_view`;
CREATE VIEW document_view AS
  (SELECT
     document_code 	documentCode,
     document_name 	documentName,
     is_provided 		isProvided
   FROM document );


DROP TABLE IF EXISTS `industry`;
CREATE TABLE `industry` (
  `code` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `occupation_class`;
CREATE TABLE `occupation_class` (
 `occupation_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`occupation_id`),
  UNIQUE KEY (`code`,`description`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `designation`;
CREATE TABLE `designation` (
  `code` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `plan_coverage_benefits_assoc`;
CREATE TABLE `plan_coverage_benefits_assoc` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `plan_id` varchar(60) NOT NULL,
  `coverage_id` varchar(255) NOT NULL,
  `benefit_id` varchar(255) NOT NULL,
  `optional` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_PLAN_BENEFIT_ID` (`benefit_id`),
  KEY `FK_PLAN_COVERAGE_ID` (`coverage_id`),
  CONSTRAINT `FK_PLAN_BENEFIT_ID` FOREIGN KEY (`benefit_id`) REFERENCES `benefit` (`benefit_id`),
  CONSTRAINT `FK_PLAN_COVERAGE_ID` FOREIGN KEY (`coverage_id`) REFERENCES `coverage` (`coverage_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

ALTER TABLE `plan_coverage_benefits_assoc`
  ADD COLUMN `plan_name` VARCHAR(100) NULL AFTER `plan_id`,
  ADD COLUMN `plan_code` VARCHAR(10) NULL AFTER `plan_name`,
  ADD COLUMN `launch_date` date NOT NULL AFTER `plan_code`,
  ADD COLUMN `withdrawal_date` date AFTER `launch_date`,
  ADD COLUMN `client_type` varchar(60) NOT NULL AFTER `withdrawal_date`,
  ADD COLUMN `line_of_business` varchar(60) NOT NULL AFTER `client_type`,
  ADD COLUMN `funeral_cover` tinyint(1) DEFAULT NULL;

DROP VIEW IF EXISTS `plan_coverage_benefit_assoc_view`;
CREATE  VIEW `plan_coverage_benefit_assoc_view` AS
  ( SELECT
      plan_id                          planId,
      plan_name                        planName,
      plan_code                        planCode,
      launch_date                      launchDate,
      withdrawal_date                  withdrawalDate,
      client_type                      clientType,
      line_of_business                 lineOfBusinessId,
      coverage_id                      coverageId,
      benefit_id                       benefitId,
      optional                         optional,
      funeral_cover                    funeralCover
    FROM plan_coverage_benefits_assoc ORDER BY plan_name,launch_date );

DROP TABLE IF EXISTS `endorsement_type`;
CREATE TABLE `endorsement_type` (
  `endorsement_type_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(120) NOT NULL,
  `category` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`endorsement_type_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

ALTER TABLE `endorsement_type`
  ADD  UNIQUE INDEX `UNIQUE` (`description`, `category`);

DROP TABLE IF EXISTS `individual_life_quotation`;
  CREATE TABLE `individual_life_quotation` (
    `quotation_id` varchar(255) NOT NULL,
    `version` bigint(20) DEFAULT NULL,
    `agent_id` varchar(255) DEFAULT NULL,
    `generated_on` tinyblob,
    `il_quotation_status` varchar(20) DEFAULT NULL,
    `is_assured_the_proposer` tinyint(1) NOT NULL DEFAULT '0',
    `plan_id` varchar(255) DEFAULT NULL,
    `quotation_creator` varchar(255) DEFAULT NULL,
    `quotation_number` varchar(255) DEFAULT NULL,
    `version_number` int(11) NOT NULL,
    PRIMARY KEY (`quotation_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `assured`;
  CREATE TABLE `assured` (
    `assured_id` varchar(255) NOT NULL,
    `age_next_birth_day` tinyblob,
    `assuredfname` varchar(255) DEFAULT NULL,
    `assurednrc` varchar(255) DEFAULT NULL,
    `assured_surname` varchar(255) DEFAULT NULL,
    `assured_title` varchar(255) DEFAULT NULL,
    `date_of_birth` date DEFAULT NULL,
    `email_id` varchar(255) DEFAULT NULL,
    `gender` varchar(11) DEFAULT NULL,
    `mobile_number` varchar(255) DEFAULT NULL,
    `occupation` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`assured_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `quotation_assured`;
  CREATE TABLE `quotation_assured` (
   `quotation_id` varchar(255) NOT NULL,
   `assured_id` varchar(255) NOT NULL,
   KEY `FK_QUOTATION_ID` (`quotation_id`),
   CONSTRAINT `FK_QUOTATION_QUOTATION_ID` FOREIGN KEY (`quotation_id`) REFERENCES `individual_life_quotation` (`quotation_id`),
   CONSTRAINT `FK_ASSURED_ASSURED_ID` FOREIGN KEY (`assured_id`) REFERENCES `assured` (`assured_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `proposer`;
  CREATE TABLE `proposer` (
    `proposer_id` varchar(255) NOT NULL,
    `age_next_birth_day` tinyblob,
    `date_of_birth` date DEFAULT NULL,
    `email_id` varchar(255) DEFAULT NULL,
    `gender` varchar(11) DEFAULT NULL,
    `mobile_number` varchar(255) DEFAULT NULL,
    `proposerfname` varchar(255) DEFAULT NULL,
    `proposernrc` varchar(255) DEFAULT NULL,
    `proposer_surname` varchar(255) DEFAULT NULL,
    `proposer_title` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`proposer_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `plan_detail`;
  CREATE TABLE `plan_detail` (
    `plan_detail_id` varchar(255) NOT NULL,
    `plan_id` varchar(255) DEFAULT NULL,
    `policy_term` int(11) DEFAULT NULL,
    `premium_payment_term` int(11) DEFAULT NULL,
    `sum_assured` decimal(19,2) DEFAULT NULL,
    PRIMARY KEY (`plan_detail_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rider_detail`;
  CREATE TABLE `rider_detail` (
    `rider_detail_id` varchar(255) NOT NULL,
    `cover_term` int(11) DEFAULT NULL,
    `coverage_id` varchar(255) DEFAULT NULL,
    `sum_assured` decimal(19,2) DEFAULT NULL,
    `waiver_of_premium` int(11) DEFAULT NULL,
    PRIMARY KEY (`rider_detail_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `palndetail_rider`;
  CREATE TABLE `palndetail_rider` (
    `plan_detail_id` varchar(255) NOT NULL,
    `rider_detail_id` varchar(255) NOT NULL,
    PRIMARY KEY (`plan_detail_id`,`rider_detail_id`),
    UNIQUE KEY `UK_RAIDER_DETAIL` (`rider_detail_id`),
    CONSTRAINT `FK_PLAN_DETAIL_PLAN_ID` FOREIGN KEY (`plan_detail_id`) REFERENCES `plan_detail` (`plan_detail_id`),
    CONSTRAINT `FK_RIDER_RIDER_ID` FOREIGN KEY (`rider_detail_id`) REFERENCES `rider_detail` (`rider_detail_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `quotation_plandetail`;
  CREATE TABLE `quotation_plandetail` (
    `plan_detail_id` varchar(255) DEFAULT NULL,
    `quotation_id` varchar(255) NOT NULL,
    PRIMARY KEY (`quotation_id`),
    KEY `FK_QUOTATION_PLANDETAIL_ID` (`plan_detail_id`),
    CONSTRAINT `FK_QUOTATION_QUOTATION_PLAN_ID` FOREIGN KEY (`quotation_id`) REFERENCES `individual_life_quotation` (`quotation_id`),
    CONSTRAINT `FK_PLAN_DETAIL_QUOTATION_ID` FOREIGN KEY (`plan_detail_id`) REFERENCES `plan_detail` (`plan_detail_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `employment_type`;
CREATE TABLE `employment_type` (
 `employment_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`employment_id`),
  UNIQUE KEY (`code`,`description`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `plan_coverage_benefit_assoc`;
CREATE TABLE `plan_coverage_benefit_assoc` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`plan_id` varchar(60) NOT NULL,
`coverage_id` varchar(255) NOT NULL,
`benefit_id` varchar(255) NOT NULL,
`optional` tinyint(1) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `FK_PLAN_BENEFIT_ID` (`benefit_id`),
KEY `FK_PLAN_COVERAGE_ID` (`coverage_id`),
CONSTRAINT `FK_PLAN_BENEFIT_ID` FOREIGN KEY (`benefit_id`) REFERENCES `benefit` (`benefit_id`),
CONSTRAINT `FK_PLAN_COVERAGE_ID` FOREIGN KEY (`coverage_id`) REFERENCES `coverage` (`coverage_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

ALTER TABLE `plan_coverage_benefits_assoc`
ADD COLUMN `plan_name` VARCHAR(100) NULL AFTER `plan_id`,
ADD COLUMN `plan_code` VARCHAR(10) NULL AFTER `plan_name`,
ADD COLUMN `launch_date` date NOT NULL AFTER `plan_code`,
ADD COLUMN `withdrawal_date` date AFTER `launch_date`,
ADD COLUMN `client_type` varchar(60) NOT NULL AFTER `withdrawal_date`,
ADD COLUMN `line_of_business` varchar(60) NOT NULL AFTER `client_type`,
ADD COLUMN `funeral_cover` tinyint(1) DEFAULT NULL;

DROP TABLE IF EXISTS `individual_life_quotation`;
 CREATE TABLE `individual_life_quotation` (
   `quotation_id` varchar(255) NOT NULL,
   `last_event_sequence_number` bigint(20) DEFAULT NULL,
   `version` bigint(20) DEFAULT NULL,
   `agent_id` varchar(255) DEFAULT NULL,
   `generated_on` tinyblob,
   `il_quotation_status` varchar(255) DEFAULT NULL,
   `is_assured_the_proposer` tinyint(1) DEFAULT '0',
   `parent_quotation_id` varchar(255) DEFAULT NULL,
   `plan_id` varchar(255) DEFAULT NULL,
   `policy_term` int(11) DEFAULT NULL,
   `premium_payment_term` int(11) DEFAULT NULL,
   `sum_assured` decimal(19,2) DEFAULT NULL,
   `age_next_birth_day` tinyblob,
   `assured_date_of_birth` date DEFAULT NULL,
   `assured_email_id` varchar(255) DEFAULT NULL,
   `assuredfname` varchar(255) DEFAULT NULL,
   `assured_gender` varchar(255) DEFAULT NULL,
   `assured_mobile_number` varchar(255) DEFAULT NULL,
   `assurednrc` varchar(255) DEFAULT NULL,
   `assured_surname` varchar(255) DEFAULT NULL,
   `assured_title` varchar(255) DEFAULT NULL,
   `occupation` varchar(255) DEFAULT NULL,
   `gender` varchar(255) DEFAULT NULL,
   `proposer_date_of_birth` date DEFAULT NULL,
   `proposer_email_id` varchar(255) DEFAULT NULL,
   `proposerfname` varchar(255) DEFAULT NULL,
   `proposer_mobile_number` varchar(255) DEFAULT NULL,
   `proposernrc` varchar(255) DEFAULT NULL,
   `proposer_surname` varchar(255) DEFAULT NULL,
   `proposer_title` varchar(255) DEFAULT NULL,
   `quotation_creator` varchar(255) DEFAULT NULL,
   `quotation_number` varchar(255) DEFAULT NULL,
   `version_number` int(11) NOT NULL,
  PRIMARY KEY (`quotation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `individual_life_quotation_rider_details`;
 CREATE TABLE `individual_life_quotation_rider_details` (
   `individual_life_quotation_quotationId` varchar(255) NOT NULL,
   `cover_term` int(11) DEFAULT NULL,
   `coverage_id` varchar(255) DEFAULT NULL,
   `sum_assured` decimal(19,2) DEFAULT NULL,
   `waiver_of_premium` int(11) DEFAULT NULL,
   KEY `FK_QUOTATION_RIDER` (`individual_life_quotation_quotationId`),
   CONSTRAINT `FK_QUOTATION_QUOTATON_ID` FOREIGN KEY (`individual_life_quotation_quotationId`) REFERENCES `individual_life_quotation` (`quotation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
