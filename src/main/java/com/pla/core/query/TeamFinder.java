/*
 * Copyright (c) 3/5/15 4:08 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.google.common.base.Preconditions;
import com.pla.core.dto.TeamDto;
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

    public static final String ACTIVE_TEAM_COUNT_BY_TEAM_NAME_QUERY = "select count(team_id) from team where team_name=:teamName and active = '1'";
    public static final String ACTIVE_TEAM_COUNT_BY_TEAM_CODE_QUERY = "select count(team_id) from team where team_code=:teamCode and active = '1'";
    public static final String ACTIVE_TEAM_COUNT_BY_TEAM_ASSOCIATED_WITH_AGENT_CODE_QUERY = "select count(agent_id) from agent where team_id=:teamCode ";
    public static final String FIND_TEAM_BY_ID_QUERY = "SELECT tm.team_id AS teamId,tm.team_name AS teamName,tm.team_code AS teamCode,tm.current_team_leader AS currentTeamLeader,tf.first_Name AS firstName,\n" +
            " tf.last_Name AS lastName,tf.from_date AS fromDate,tf.thru_date AS endDate ,b.branch_name AS branchName,r.region_name AS regionName,b.branch_code AS branchCode,r.region_code AS regionCode \n" +
            " FROM team tm \n" +
            " INNER  JOIN team_team_leader_fulfillment tf ON  tm.current_team_leader=tf.employee_id AND tf.team_id = tm.team_id  AND tf.thru_date IS NULL\n" +
            " \n" +
            " INNER JOIN region r ON  tm.region_code=r.region_code \n" +
            " INNER JOIN branch b ON  tm.branch_code=b.branch_code WHERE tm.team_id=:teamId";
    public static final String FIND_ALL_ACTIVE_TEAM_QUERY = "select * from active_team_region_branch_view";
    public static final String FIND_ALL_ACTIVE_TEAM_LEADER_QUERY = "SELECT tm.team_id AS teamId,tm.team_name AS teamName,tm.team_code AS teamCode,tf.employee_id AS currentTeamLeader,tf.first_Name AS firstName, " +
            " tf.last_Name AS lastName,tf.from_date AS fromDate,tf.thru_date AS endDate " +
            " FROM team tm " +
            " LEFT OUTER JOIN team_team_leader_fulfillment tf  ON tf.team_id = tm.team_id  AND ((tf.thru_date >= (CURDATE()+1)) OR (tf.thru_date IS NULL)) " +
            " WHERE tm.active='1'";
    public static final String FIND_ALL_ACTIVE_TEAM_FULFILLMENT_GREATER_THAN_CURRENT_DATE_QUERY = "select * from active_team_team_fulfillment_greater_than_current_date";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int getTeamCountByTeamName(String teamName) {
        Preconditions.checkNotNull(teamName);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(ACTIVE_TEAM_COUNT_BY_TEAM_NAME_QUERY, new MapSqlParameterSource().addValue("teamName", teamName), Number.class);
        return noOfBenefit.intValue();
    }

    public int getTeamCountByTeamCode(String teamCode) {
        Preconditions.checkNotNull(teamCode);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(ACTIVE_TEAM_COUNT_BY_TEAM_CODE_QUERY, new MapSqlParameterSource().addValue("teamCode", teamCode), Number.class);
        return noOfBenefit.intValue();
    }

    public int getActiveTeamCountByAgentAssociatedWithTeam(TeamDto teamDto) {
        Preconditions.checkNotNull(teamDto);
        Number noOfActiveTeamCountByAgentAssociatedWithTeam = namedParameterJdbcTemplate.queryForObject(ACTIVE_TEAM_COUNT_BY_TEAM_ASSOCIATED_WITH_AGENT_CODE_QUERY, new MapSqlParameterSource().addValue("teamCode", teamDto.getTeamId()), Number.class);
        return noOfActiveTeamCountByAgentAssociatedWithTeam.intValue();
    }

    public Map<String, Object> getTeamById(String teamId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_TEAM_BY_ID_QUERY, new MapSqlParameterSource().addValue("teamId", teamId));
    }

    public List<Map<String, Object>> getAllActiveTeam() {
        return namedParameterJdbcTemplate.query(FIND_ALL_ACTIVE_TEAM_QUERY, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllActiveTeamFulfillmentGreaterThanCurrentDate() {
        return namedParameterJdbcTemplate.query(FIND_ALL_ACTIVE_TEAM_FULFILLMENT_GREATER_THAN_CURRENT_DATE_QUERY, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllActiveTeamLeaders() {
        return namedParameterJdbcTemplate.query(FIND_ALL_ACTIVE_TEAM_LEADER_QUERY, new ColumnMapRowMapper());
    }
}
