/*
 * Copyright (c) 3/5/15 4:08 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.google.common.base.Preconditions;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Finder
@Service
public class BenefitFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static final String findBenefitForAGivenBenefitName = "select * from benefit where benefit_name=:benefitName";

    public static final String activeBenefitCountByBenefitName = "select count(benefit_id) from benefit where benefit_name=:benefitName";

    public static final String findBenefitForAGivenBenefitId = "SELECT benefit_id AS benefitId,benefit_name AS benefitName,STATUS AS benefitStatus FROM benefit where benefit_id=:benefitId";

    public static final String findAllBenefit = "SELECT benefit_id AS benefitId,benefit_name AS benefitName,STATUS AS benefitStatus FROM benefit";

    public List<Map<String, Object>> findBenefitFor(String benefitName) {
        return namedParameterJdbcTemplate.query(findBenefitForAGivenBenefitName, Collections.singletonMap("benefitName", benefitName), new ColumnMapRowMapper());
    }

    public int getBenefitCountByBenefitName(String benefitName) {
        Preconditions.checkNotNull(benefitName);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(activeBenefitCountByBenefitName, new MapSqlParameterSource().addValue("benefitName", benefitName), Number.class);
        return noOfBenefit.intValue();
    }

    public Map<String, Object> findBenefitById(String benefitId) {
        return namedParameterJdbcTemplate.queryForMap(findBenefitForAGivenBenefitId, new MapSqlParameterSource().addValue("benefitId", benefitId));
    }

    public List<Map<String, Object>> getAllBenefit() {
        return namedParameterJdbcTemplate.query(findAllBenefit, new ColumnMapRowMapper());
    }
}
