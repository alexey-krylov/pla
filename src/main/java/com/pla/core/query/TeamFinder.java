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

    public static final String ACTIVE_BENEFIT_COUNT_BY_TEAM_NAMEQuery = "select count(team_id) from team where team_name=:teamName";

    public static final String ACTIVE_BENEFIT_COUNT_BY_TEAM_CODEQuery = "select count(team_id) from team where team_code=:teamCode";

    public static final String findAllTeamQuery = "SELECT tm.team_id AS teamId,tm.team_name AS teamName,tm.team_code as teamCode,tf.first_Name AS firstName," +
            "tf.last_Name AS lastName,tf.from_date AS fromDate,b.branch_name AS branchName,r.regional_manager AS regionalManager,r.region_name AS regionName,b.BRANCH_CODE AS branchCode " +
            "FROM team tm " +
            "LEFT JOIN region r ON r.region_code = tm.region_code " +
            "LEFT JOIN branch b ON b.branch_code = tm.branch_code " +
            "LEFT JOIN team_team_leader_fulfillment tf ON tf.employee_id = tm.current_team_leader";


       public static final String FIND_TEAM_BY_ID_QUERY = "SELECT tm.current_team_leader AS currentTeamLeader ,tm.team_id AS teamId,tm.team_name AS teamName,b.branch_name AS branchName,r.region_name AS regionName,r.regional_manager AS regionalManager,r.REGION_CODE AS regionCode,b.BRANCH_CODE AS branchCode FROM team tm " +
            "LEFT JOIN region r ON r.region_code = tm.region_code " +
            "LEFT JOIN branch b ON b.branch_code = tm.branch_code  " +
            "RIGHT JOIN team_team_leader_fulfillment ttlf ON ttlf.team_id = tm.team_id WHERE tm.team_id=:teamId ";


    public static final String FIND_ALL_ACTIVE_TEAM_QUERY = "select * from active_team_region_branch_view";

    public int getTeamCountByTeamName(String teamName) {
        Preconditions.checkNotNull(teamName);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(ACTIVE_BENEFIT_COUNT_BY_TEAM_NAMEQuery, new MapSqlParameterSource().addValue("teamName", teamName), Number.class);
        return noOfBenefit.intValue();
    }

    public int getTeamCountByTeamCode(String teamCode) {
        Preconditions.checkNotNull(teamCode);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(ACTIVE_BENEFIT_COUNT_BY_TEAM_CODE_QUERY, new MapSqlParameterSource().addValue("teamCode", teamCode), Number.class);
        return noOfBenefit.intValue();
    }

    public List<Map<String, Object>> getAllTeam() {
        return namedParameterJdbcTemplate.query(findAllTeamQuery, new ColumnMapRowMapper());
    }

    public Map<String, Object> getTeamById(String teamId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_TEAM_BY_ID_QUERY, new MapSqlParameterSource().addValue("teamId", teamId));
    }

    public List<Map<String, Object>> getAllActiveTeam() {
        return namedParameterJdbcTemplate.query(FIND_ALL_ACTIVE_TEAM_QUERY, new ColumnMapRowMapper());
    }
}
