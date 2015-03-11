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
public class TeamFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

  //  public static final String FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_NAME = "select * from team where team_name=:teamName";

    public static final String ACTIVE_BENEFIT_COUNT_BY_TEAM_NAMECODE = "select count(team_id) from team where team_name=:teamName";

   /* public List<Map<String, Object>> findBenefitFor(String benefitName) {
        return namedParameterJdbcTemplate.query(FIND_BENEFIT_FOR_A_GIVEN_BENEFIT_NAME, Collections.singletonMap("benefitName", benefitName), new ColumnMapRowMapper());
    }*/
    
    public int getTeamCountByTeamNameCode(String teamName, String teamCode){
        Preconditions.checkNotNull(teamName);
        Preconditions.checkNotNull(teamCode);
        Number noOfBenefit  = namedParameterJdbcTemplate.queryForObject(ACTIVE_BENEFIT_COUNT_BY_TEAM_NAMECODE, new MapSqlParameterSource().addValue("teamName",teamName).addValue("teamCode",teamCode), Number.class);
        return noOfBenefit.intValue();
    }
}
