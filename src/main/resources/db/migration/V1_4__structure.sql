USE PLA;

DROP TABLE IF EXISTS `notification_role`;
CREATE TABLE `notification_role` (
  `line_of_business` varchar(255) NOT NULL,
  `process` varchar(255) NOT NULL,
  `role_type` varchar(255) NOT NULL,
  PRIMARY KEY (`line_of_business`,`process`,`role_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;