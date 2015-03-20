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

    public static final String ACTIVE_BENEFIT_COUNT_BY_TEAM_NAME = "select count(team_id) from team where team_name=:teamName";

    public static final String ACTIVE_BENEFIT_COUNT_BY_TEAM_CODE = "select count(team_id) from team where team_code=:teamCode";

    public static final String findAllTeam = "SELECT tm.team_id AS teamId,tm.team_name AS teamName,tm.team_code as teamCode,tf.first_Name AS firstName," +
            "tf.last_Name as lastName,tf.from_date AS fromDate,b.branch as branchName,r.regional_manager as regionalManager,r.region AS regionName,b.BRANCH_CODE AS branchCode " +
            "FROM team tm " +
            "LEFT JOIN region r ON r.region_code = tm.region_code " +
            "LEFT JOIN branch b ON b.branch_code = tm.branch_code " +
            "LEFT JOIN team_team_leader_fulfillment tf ON tf.employee_id = tm.current_team_leader";



    public int getTeamCountByTeamName(String teamName) {
        Preconditions.checkNotNull(teamName);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(ACTIVE_BENEFIT_COUNT_BY_TEAM_NAME, new MapSqlParameterSource().addValue("teamName", teamName), Number.class);
        return noOfBenefit.intValue();
    }

    public int getTeamCountByTeamCode(String teamCode) {
        Preconditions.checkNotNull(teamCode);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(ACTIVE_BENEFIT_COUNT_BY_TEAM_CODE, new MapSqlParameterSource().addValue("teamCode", teamCode), Number.class);
        return noOfBenefit.intValue();
    }

    public List<Map<String, Object>> getAllTeam() {
        return namedParameterJdbcTemplate.query(findAllTeam, new ColumnMapRowMapper());
    }


}
