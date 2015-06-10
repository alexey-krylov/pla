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

    public static final String ACTIVE_BENEFIT_COUNT_BY_BENEFIT_NAME_Query = "select count(benefit_id) from benefit where benefit_name=:benefitName and status='ACTIVE' " +
            " and benefit_id !=:benefitId";

    public static final String ACTIVE_BENEFIT_COUNT_BY_BENEFIT_CODE_Query = "select count(benefit_id) from benefit where benefit_code=:benefitCode and status='ACTIVE' " +
            " and benefit_id !=:benefitId";

    public static final String FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_ID_Query = "SELECT benefit_id AS benefitId,benefit_name AS benefitName,STATUS AS benefitStatus,IFNULL(benefit_code,'') benefitCode FROM benefit where benefit_id=:benefitId";

    public static final String FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_CODE_QUERY = "SELECT benefit_id AS benefitId,benefit_name AS benefitName,STATUS AS benefitStatus,IFNULL(benefit_code,'') benefitCode FROM benefit where benefit_code=:benefitCode";

    public static final String FIND_ALL_BENEFIT_Query = "SELECT benefit_id AS benefitId,IFNULL(benefit_code,'') benefitCode,benefit_name AS benefitName,STATUS AS benefitStatus FROM benefit";

    public static final String BENEFIT_COUNT_ASSOCIATED_WITH_ACTIVE_COVERAGE_Query = "SELECT COUNT(CB.benefit_id) FROM `coverage_benefit` CB,`coverage` C WHERE CB.coverage_id=C.coverage_id AND C.status IN('ACTIVE','INUSE') AND CB.benefit_id=:benefitId";

    public static final String FIND_ALL_ACTIVE_BENEFITS_Query = "SELECT benefit_id benefitId,STATUS STATUS ,benefit_name benefitName, IFNULL(benefit_code,'') benefitCode FROM " +
            " benefit WHERE STATUS='ACTIVE'";

    public List<Map<String, Object>> findBenefitFor(String benefitName) {
        return namedParameterJdbcTemplate.query(FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_NAME_Query, Collections.singletonMap("benefitName", benefitName), new ColumnMapRowMapper());
    }

    public int getBenefitCountByBenefitName(String benefitName,String benefitId) {
        Preconditions.checkNotNull(benefitName);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(ACTIVE_BENEFIT_COUNT_BY_BENEFIT_NAME_Query, new MapSqlParameterSource("benefitId",benefitId).addValue("benefitName", benefitName), Number.class);
        return noOfBenefit.intValue();
    }

    public int getBenefitCountByBenefitCode(String benefitCode,String benefitId) {
        Preconditions.checkNotNull(benefitCode);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(ACTIVE_BENEFIT_COUNT_BY_BENEFIT_CODE_Query, new MapSqlParameterSource("benefitId",benefitId).addValue("benefitCode",benefitCode), Number.class);
        return noOfBenefit.intValue();
    }

    public Map<String, Object> findBenefitById(String benefitId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_ID_Query, new MapSqlParameterSource().addValue("benefitId", benefitId));
    }


    public Map<String, Object> findBenefitByCode(String benefitCode) {
        return namedParameterJdbcTemplate.queryForMap(FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_CODE_QUERY, new MapSqlParameterSource().addValue("benefitCode", benefitCode));
    }

    public List<Map<String, Object>> getAllBenefit() {
        return namedParameterJdbcTemplate.query(FIND_ALL_BENEFIT_Query, new ColumnMapRowMapper());
    }

    public int getBenefitCountAssociatedWithActiveCoverage(String benefitId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(benefitId));
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(BENEFIT_COUNT_ASSOCIATED_WITH_ACTIVE_COVERAGE_Query, new MapSqlParameterSource().addValue("benefitId", benefitId), Number.class);
        return noOfBenefit.intValue();
    }

    public List<Map<String, Object>> getAllActiveBenefit() {
        return namedParameterJdbcTemplate.query(FIND_ALL_ACTIVE_BENEFITS_Query, new ColumnMapRowMapper());
    }
}
