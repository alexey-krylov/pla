INSERT INTO `agent` (`agent_id`, `designation_code`, `designation_name`, `employee_id`, `first_name`, `last_name`, `nrc_number`, `title`, `training_complete_on`, `agent_status`, `channel_code`, `channel_name`, `address_line1`, `address_line2`, `email`, `city`, `postal_code`, `province`, `home_phone_number`, `mobile_number`, `work_phone_number`, `license_number`, `override_commission_applicable`, `physical_address_line1`, `physical_address_line2`, `physical_address_city`, `physical_address_postal_code`, `physical_address_province`, `team_id`) VALUES('000000','Direct','Direct',NULL,'DirectF','DirectL',NULL,NULL,NULL,'ACTIVE','DIRECT','Direct',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert  into `entity_sequence`(`sequence_id`,`sequence_name`,`sequence_number`,`sequence_prefix`) values (7,'com.pla.core.domain.model.Benefit',0,'1000');
insert  into `entity_sequence`(`sequence_id`,`sequence_name`,`sequence_number`,`sequence_prefix`) values (8,'com.pla.core.domain.model.Coverage',0,'1000');