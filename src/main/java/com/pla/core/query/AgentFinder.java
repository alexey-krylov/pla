/*
 * Copyright (c) 3/16/15 6:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.pla.core.dto.AgentDto;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.nthdimenzion.utils.UtilValidator;
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
 * @since 1.0 16/03/2015
 */
@Finder
@Service
public class AgentFinder {

    public static final String FIND_AGENT_COUNT_FOR_A_GIVEN_LICENSE_NUMBER_QUERY = "SELECT COUNT(agent_id) FROM agent WHERE license_number =:licenseNumber";

    public static final String FIND_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId";

    public static final String FIND_AGENT_COUNT_BY_NRC_NUMBER_QUERY = "SELECT COUNT(agent_id) FROM agent WHERE nrc_number = :nrcNumber and agent_id != :agentId";

    public static final String FIND_ALL_AGENT_BY_STATUS_QUERY = "select * from agent_team_branch_view where agentStatus IN (:agentStatuses)";

    public static final String FIND_ALL_BROKER_BY_STATUS_QUERY = "select * from agent_team_branch_view where agentStatus IN (:agentStatuses) and channelCode='BROKER'";

    public static final String FIND_AGENT_PLAN_QUERY = "SELECT agent_id AS agentId,plan_id AS planId FROM `agent_authorized_plan`";

    public static final String SEARCH_AGENT_BY_PLAN_LOB = "SELECT A.*,c.line_of_business FROM AGENT A JOIN agent_authorized_plan b " +
            " ON A.`agent_id`=B.`agent_id` JOIN plan_coverage_benefit_assoc C " +
            " ON B.`plan_id`=C.`plan_id` where c.line_of_business=:lineOfBusiness and A.agent_status='ACTIVE' group by A.agent_id";

    public static final String FIND_AGENT_BY_NRC_NUMBER_QUERY = " SELECT COUNT(agent_id) FROM agent WHERE nrc_number= :nrcNumber ";

    /**
     * Find all the Plans by Agent Id and for a line of business.
     */
    private static final String SEARCH_PLAN_BY_AGENT_ID = "SELECT C.* FROM AGENT A JOIN agent_authorized_plan b " +
            "ON A.`agent_id`=B.`agent_id` JOIN plan_coverage_benefit_assoc C " +
            "ON B.`plan_id`=C.`plan_id` where A.agent_id=:agentId and c.line_of_business=:lineOfBusiness group by C.plan_id";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int getAgentCountByLicenseNumber(String licenseNumber) {
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(FIND_AGENT_COUNT_FOR_A_GIVEN_LICENSE_NUMBER_QUERY, new MapSqlParameterSource().addValue("licenseNumber", licenseNumber), Number.class);
        return noOfBenefit.intValue();
    }


    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
    }

    public List<Map<String, Object>> getAllAgentPlan() {
        return namedParameterJdbcTemplate.query(FIND_AGENT_PLAN_QUERY, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllNonTerminatedAgent() {
        return namedParameterJdbcTemplate.query(FIND_ALL_AGENT_BY_STATUS_QUERY, new MapSqlParameterSource().addValue("agentStatuses", Lists.newArrayList("ACTIVE", "INACTIVE")), new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllNonTerminatedBrokers() {
        return namedParameterJdbcTemplate.query(FIND_ALL_BROKER_BY_STATUS_QUERY, new MapSqlParameterSource().addValue("agentStatuses", Lists.newArrayList("ACTIVE", "INACTIVE")), new ColumnMapRowMapper());
    }


    public int getAgentCountByNrcNumber(AgentDto agentDto) {
        Number noOfAgentCount = namedParameterJdbcTemplate.queryForObject(FIND_AGENT_COUNT_BY_NRC_NUMBER_QUERY, new MapSqlParameterSource().addValue("nrcNumber", agentDto.getNrcNumber()).addValue("agentId", agentDto.getAgentId()), Number.class);
        return noOfAgentCount.intValue();
    }


    public List<Map<String, Object>> searchAgent(String searchStr) {
        return namedParameterJdbcTemplate.query(SEARCH_AGENT_BY_PLAN_LOB, new MapSqlParameterSource().addValue("lineOfBusiness", "Individual Life"), new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> searchPlanByAgentId(String agentId) {
        return namedParameterJdbcTemplate.query(SEARCH_PLAN_BY_AGENT_ID, new MapSqlParameterSource().addValue("agentId", agentId).addValue("lineOfBusiness", "Individual Life"), new ColumnMapRowMapper());
    }

    public Integer findAgentCountByNrcNumber(String nrcNumber){
        Number count = namedParameterJdbcTemplate.queryForObject(FIND_AGENT_BY_NRC_NUMBER_QUERY,new MapSqlParameterSource("nrcNumber",nrcNumber),Number.class);
        return count.intValue();
    }
}
