USE PLA;


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

CREATE TABLE `saga_entry` (
  `saga_id`         VARCHAR(255) NOT NULL,
  `revision`        VARCHAR(255) DEFAULT NULL,
  `saga_type`       VARCHAR(255) DEFAULT NULL,
  `serialized_saga` LONGBLOB,
  PRIMARY KEY (`saga_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

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


-- DROP TABLE IF EXISTS region;
-- DROP TABLE IF EXISTS geo;
-- DROP TABLE IF EXISTS channel_type;
-- DROP TABLE IF EXISTS bank_name;
-- DROP TABLE IF EXISTS bank_branch;
-- DROP TABLE IF EXISTS hcp_service;
/*
geo table stores the geographical mapping of the country
GEO_ID is a sequential number which refers to GEO_TYPE
PARENT_GEO_ID points to its parent GEO_ID
GEO_TYPE will be the type of geographical mapping.
	possible values are provinces, city , suburb, zip code
GEO_DESCRIPTION is the name of the provinces , city , suburb or the zip code

*/
CREATE TABLE geo (
  GEO_ID        VARCHAR(20) NOT NULL,
  PARENT_GEO_ID VARCHAR(20),
  GEO_TYPE      VARCHAR(20) NOT NULL,
  GEO_DESCRIPTION VARCHAR(100) NOT NULL,
  PRIMARY KEY (GEO_ID)
);

/*
channel_type stores the different type of channel from which a policy can be taken
CHANNEL_CODE is a sequential number
CHANNEL_DESCRIPTION is the string representation of channel type
*/
CREATE TABLE channel_type (
  CHANNEL_CODE VARCHAR(20),
  CHANNEL_DESCRIPTION VARCHAR(50) NOT NULL UNIQUE,
  PRIMARY KEY (CHANNEL_CODE)
);

/*
BANK_CODE a sequential number which references a bank name
BANK_NAME String representation of the bank name
*/

CREATE TABLE bank_name (
  BANK_CODE VARCHAR(20),
  BANK_NAME VARCHAR(50) NOT NULL UNIQUE,
  PRIMARY KEY (BANK_CODE)
);

/*
BANK_CODE is the code which maps to a bank name
BRANCH is the branch to which the bank belongs to
SORT_CODE is a unique code assigned to each branch
*/
CREATE TABLE bank_branch (
  BANK_CODE VARCHAR(20)  NOT NULL,
  BRANCH    VARCHAR(100) NOT NULL,
  SORT_CODE VARCHAR(100),
  PRIMARY KEY (SORT_CODE),
  FOREIGN KEY (BANK_CODE)
  REFERENCES bank_name (BANK_CODE)
);

/*
hcp_service stores the values of the HEALTH CARE PROVIDERS
SERVICE_CODE code for each HCP
HCP_DESCRIPTION is the string representation of health care providers
HCP_TYPE is the type to which HCP providers belong to like Government Hospital, Private clinic
All other fields are self descriptive
*/

CREATE TABLE hcp_service (
  HCP_SERVICE_CODE VARCHAR(20),
  HCP_DESCRIPTION  VARCHAR(50) NOT NULL,
  HCP_TYPE         VARCHAR(50) NOT NULL,
  ADDRESS          VARCHAR(200),
  PROVINCE_GEO_ID  VARCHAR(20) NOT NULL,
  TOWN_GEO_ID      VARCHAR(20) NOT NULL,
  POSTAL_CODE_GEO_ID VARCHAR(20),
  WORK_PHONE       BIGINT(13),
  EMAIL_ADDRESS    VARCHAR(320),
  PRIMARY KEY (HCP_SERVICE_CODE),
  FOREIGN KEY (PROVINCE_GEO_ID)
  REFERENCES geo (GEO_ID),
  FOREIGN KEY (TOWN_GEO_ID)
  REFERENCES geo (GEO_ID)
);

/*
region stores the values of the region.This is used to during team and Branches configuration.
REGION_CODE code for the region
REGION is the name of the region
*/

CREATE TABLE region (
  REGION_CODE VARCHAR(20),
  REGION VARCHAR(12) NOT NULL UNIQUE,
  PRIMARY KEY (REGION_CODE)
);

#
# Quartz seems to work best with the driver mm.mysql-2.0.7-bin.jar
#
# PLEASE consider using mysql with innodb tables to avoid locking issues
#
# In your Quartz properties file, you need to set
# org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate


-- DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
-- DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
-- DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
-- DROP TABLE IF EXISTS QRTZ_LOCKS;
-- DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
-- DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
-- DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
-- DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
-- DROP TABLE IF EXISTS QRTZ_TRIGGERS;
-- DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
-- DROP TABLE IF EXISTS QRTZ_CALENDARS;


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

-- DROP TABLE IF EXISTS coverage_benefit;
-- DROP TABLE IF EXISTS coverage;
-- DROP TABLE IF EXISTS benefit;
CREATE TABLE `benefit` (
  `benefitId`   VARCHAR(255) NOT NULL,
  `benefitName` VARCHAR(100) DEFAULT NULL,
  `status`      VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`benefitId`),
  UNIQUE KEY `UNQ_BENEFIT_NAME` (`benefitName`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `coverage` (
  `coverageId`   VARCHAR(255) NOT NULL,
  `coverageName` VARCHAR(50)  DEFAULT NULL,
  `description` varchar(150) DEFAULT NULL,
  `status`       VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`coverageId`),
  UNIQUE KEY `UNQ_COVERAGE_NAME` (`coverageName`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `coverage_benefit` (
  `COVERAGE_ID` VARCHAR(255) NOT NULL,
  `BENEFIT_ID`  VARCHAR(255) NOT NULL,
  PRIMARY KEY (`COVERAGE_ID`, `BENEFIT_ID`),
  UNIQUE KEY `UK_elu7ng09myq7snv9xwwv7gtt1` (`BENEFIT_ID`),
  CONSTRAINT `FK_le93tmyfd5fphih1oitvl4i6c` FOREIGN KEY (`COVERAGE_ID`) REFERENCES `coverage` (`coverageId`),
  CONSTRAINT `FK_elu7ng09myq7snv9xwwv7gtt1` FOREIGN KEY (`BENEFIT_ID`) REFERENCES `benefit` (`benefitId`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- DROP TABLE IF EXISTS team_team_leader_fulfillment;
-- DROP TABLE IF EXISTS team;
CREATE TABLE `team` (
  `teamId`            VARCHAR(255) NOT NULL,
  `active`            BIT(1)       DEFAULT NULL,
  `currentTeamLeader` VARCHAR(255) DEFAULT NULL,
  `teamCode`          VARCHAR(255) DEFAULT NULL,
  `teamName`          VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`teamId`),
  UNIQUE KEY `UNQ_TEAM_CODE_NAME` (`teamCode`, `teamName`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `team_team_leader_fulfillment` (
  `TEAM_ID`           VARCHAR(255) NOT NULL,
  `fromDate`          DATE         DEFAULT NULL,
  `employeeId`        VARCHAR(255) DEFAULT NULL,
  `firstName`         VARCHAR(255) DEFAULT NULL,
  `lastName`          VARCHAR(255) DEFAULT NULL,
  `thruDate`          DATE         DEFAULT NULL,
  `teamLeaders_ORDER` INT(11)      NOT NULL,
  PRIMARY KEY (`TEAM_ID`, `teamLeaders_ORDER`),
  CONSTRAINT `FK_6ksygf00r70t4ek42ora2btpy` FOREIGN KEY (`TEAM_ID`) REFERENCES `team` (`teamId`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- DROP TABLE IF EXISTS `plan_coverage_benefit`;
-- DROP TABLE IF EXISTS `plan_coverage`;
-- DROP TABLE IF EXISTS `plan_payment_maturity_amounts`;
-- DROP TABLE IF EXISTS `sum_insured_values`;
-- DROP TABLE IF EXISTS `sum_assured`;
-- DROP TABLE IF EXISTS `policy_term_valid_terms`;
-- DROP TABLE IF EXISTS `policy_term`;
-- DROP TABLE IF EXISTS `premium_payment_valid_terms`;
-- DROP TABLE IF EXISTS `premium_payment`;
-- DROP TABLE IF EXISTS `plan_payment`;
-- DROP TABLE IF EXISTS `plan`;



CREATE TABLE `policy_term` (
  `id`               BIGINT(20) NOT NULL AUTO_INCREMENT,
  `max_maturity_age` INT(11)    NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `policy_term_valid_terms` (
  `policy_term_id` BIGINT(20) NOT NULL,
  `valid_terms`    INT(11) DEFAULT NULL,
  KEY `FK_policy_term` (`policy_term_id`),
  CONSTRAINT `FK_policy_term` FOREIGN KEY (`policy_term_id`) REFERENCES `policy_term` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `premium_payment` (
  `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT,
  `payment_cut_off_age` INT(11)    NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `premium_payment_valid_terms` (
  `premium_payment_id` BIGINT(20) NOT NULL,
  `valid_terms`        INT(11) DEFAULT NULL,
  KEY `FK_premium_payment` (`premium_payment_id`),
  CONSTRAINT `FK_i4nq1052vfgxnmn4umabuox8y` FOREIGN KEY (`premium_payment_id`) REFERENCES `premium_payment` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `plan_payment` (
  `id`                        BIGINT(20) NOT NULL AUTO_INCREMENT,
  `premium_payment_term_type` INT(11)    NOT NULL,
  `premium_payment_id`        BIGINT(20)          DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_plan_premium_payment` (`premium_payment_id`),
  CONSTRAINT `FK_plan_premium_payment` FOREIGN KEY (`premium_payment_id`) REFERENCES `premium_payment` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `plan_payment_maturity_amounts` (
  `plan_payment_id`                    BIGINT(20)     NOT NULL,
  `guaranteed_survival_benefit_amount` DECIMAL(19, 2) NOT NULL,
  `maturity_year`                      INT(11)        NOT NULL,
  KEY `FK_maturity_plan_payment` (`plan_payment_id`),
  CONSTRAINT `FK_maturity_plan_payment` FOREIGN KEY (`plan_payment_id`) REFERENCES `plan_payment` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `sum_assured` (
  `type` VARCHAR(31) NOT NULL,
  `id`   BIGINT(20)  NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `sum_insured_values` (
  `sum_assured_id`     BIGINT(20) NOT NULL,
  `sum_insured_values` DECIMAL(19, 2) DEFAULT NULL,
  KEY `FK_sum_insured` (`sum_assured_id`),
  CONSTRAINT `FK_sum_insured` FOREIGN KEY (`sum_assured_id`) REFERENCES `sum_assured` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `plan` (
  `plan_id`                    VARCHAR(255) NOT NULL,
  `last_event_sequence_number` BIGINT(20) DEFAULT NULL,
  `version`                    BIGINT(20) DEFAULT NULL,
  `plan_payment_id`            BIGINT(20) DEFAULT NULL,
  `policy_term_id`             BIGINT(20) DEFAULT NULL,
  `sum_assured_id`             BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (`plan_id`),
  KEY `FK_plan_payment_id` (`plan_payment_id`),
  KEY `FK_policy_term_id` (`policy_term_id`),
  KEY `FK_sum_assured` (`sum_assured_id`),
  CONSTRAINT `FK_sum_assured` FOREIGN KEY (`sum_assured_id`) REFERENCES `sum_assured` (`id`),
  CONSTRAINT `FK_policy_term_id` FOREIGN KEY (`policy_term_id`) REFERENCES `policy_term` (`id`),
  CONSTRAINT `FK_plan_payment_id` FOREIGN KEY (`plan_payment_id`) REFERENCES `plan_payment` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `plan_coverage` (
  `coverage_id`           VARCHAR(255) NOT NULL,
  `coverage_cover`        INT(11)      NOT NULL,
  `coverage_type`         INT(11)      NOT NULL,
  `deductible_amount`     DECIMAL(19, 2) DEFAULT NULL,
  `deductible_percentage` DECIMAL(19, 2) DEFAULT NULL,
  `max_age`               INT(11)      NOT NULL,
  `min_age`               INT(11)      NOT NULL,
  `tax_applicable`        BIT(1)       NOT NULL,
  `waiting_period`        INT(11)      NOT NULL,
  `plan_id`               VARCHAR(255) NOT NULL,
  PRIMARY KEY (`coverage_id`),
  KEY `FK_2mlahy4casvx18dcnwki79jbk` (`plan_id`),
  CONSTRAINT `FK_2mlahy4casvx18dcnwki79jbk` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`plan_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `plan_coverage_benefit` (
  `plan_coverage_id`      VARCHAR(255) NOT NULL,
  `benefit_id`            VARCHAR(255)   DEFAULT NULL,
  `benefit_limit`         DECIMAL(19, 2) DEFAULT NULL,
  `coverage_benefit_type` INT(11)        DEFAULT NULL,
  `defined_per`           INT(11)        DEFAULT NULL,
  `max_limit`             DECIMAL(19, 2) DEFAULT NULL,
  KEY `FK_plan_coverage` (`plan_coverage_id`),
  CONSTRAINT `FK_plan_coverage` FOREIGN KEY (`plan_coverage_id`) REFERENCES `plan_coverage` (`coverage_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
