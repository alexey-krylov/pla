USE PLA;

DROP TABLE IF EXISTS `notification_role`;
CREATE TABLE `notification_role` (
  `line_of_business` varchar(100) NOT NULL,
  `process` varchar(100) NOT NULL,
  `role_type` varchar(100) NOT NULL,
  PRIMARY KEY (`line_of_business`,`process`,`role_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
