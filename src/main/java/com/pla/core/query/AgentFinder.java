/*
 * Copyright (c) 3/16/15 6:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.google.common.base.Preconditions;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@Finder
@Service
public class AgentFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static final String FIND_AGENT_COUNT_FOR_A_GIVEN_LICENSE_NUMBER = "SELECT COUNT(agent_id) FROM agent WHERE license_number =:licenseNumber";


    public int getAgentCountByLicenseNumber(String licenseNumber) {
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(FIND_AGENT_COUNT_FOR_A_GIVEN_LICENSE_NUMBER, new MapSqlParameterSource().addValue("licenseNumber", licenseNumber), Number.class);
        return noOfBenefit.intValue();
    }

}
