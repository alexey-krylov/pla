DROP TABLE IF EXISTS benefit;
CREATE TABLE `benefit` (
  `benefit_id` varchar(255) NOT NULL,
  `active` bit(1) DEFAULT NULL,
  `benefit_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`benefit_id`),
  UNIQUE KEY `UNQ_BENEFIT_NAME` (`benefit_name`)
);