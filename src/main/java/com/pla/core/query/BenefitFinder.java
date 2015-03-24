/*
 * Copyright (c) 3/5/15 4:08 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.google.common.base.Preconditions;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.nthdimenzion.utils.UtilValidator;
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

    public static final String FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_NAME_Query = "select * from benefit where benefit_name=:benefitName";

    public static final String ACTIVE_BENEFIT_COUNT_BY_BENEFIT_NAME_Query = "select count(benefit_id) from benefit where benefit_name=:benefitName and status='ACTIVE'";

    public static final String FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_ID_Query = "SELECT benefit_id AS benefitId,benefit_name AS benefitName,STATUS AS benefitStatus FROM benefit where benefit_id=:benefitId";

    public static final String FIND_ALL_BENEFIT_Query = "SELECT benefit_id AS benefitId,benefit_name AS benefitName,STATUS AS benefitStatus FROM benefit";

    public static final String BENEFIT_COUNT_ASSOCIATED_WITH_ACTIVE_COVERAGE_Query = "SELECT COUNT(CB.benefit_id) FROM `coverage_benefit` CB,`coverage` C WHERE CB.coverage_id=C.coverage_id AND C.status IN('ACTIVE','INUSE') AND CB.benefit_id=:benefitId";

    public List<Map<String, Object>> findBenefitFor(String benefitName) {
        return namedParameterJdbcTemplate.query(FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_NAME_Query, Collections.singletonMap("benefitName", benefitName), new ColumnMapRowMapper());
    }

    public int getBenefitCountByBenefitName(String benefitName) {
        Preconditions.checkNotNull(benefitName);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(ACTIVE_BENEFIT_COUNT_BY_BENEFIT_NAME_Query, new MapSqlParameterSource().addValue("benefitName", benefitName), Number.class);
        return noOfBenefit.intValue();
    }

    public Map<String, Object> findBenefitById(String benefitId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_ID_Query, new MapSqlParameterSource().addValue("benefitId", benefitId));
    }

    public List<Map<String, Object>> getAllBenefit() {
        return namedParameterJdbcTemplate.query(FIND_ALL_BENEFIT_Query, new ColumnMapRowMapper());
    }

    public int getBenefitCountAssociatedWithActiveCoverage(String benefitId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(benefitId));
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(BENEFIT_COUNT_ASSOCIATED_WITH_ACTIVE_COVERAGE_Query, new MapSqlParameterSource().addValue("benefitId", benefitId), Number.class);
        return noOfBenefit.intValue();
    }
}
