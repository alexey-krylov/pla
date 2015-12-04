/*
 * Copyright (c) 3/16/15 6:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.dto.AgentDto;
import com.pla.sharedkernel.domain.model.PlanStatus;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

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

    public static final String FIND_AGENT_PLAN = "SELECT a.agent_id AS agentId,a.plan_id AS planId FROM `agent_authorized_plan` a INNER JOIN plan_coverage_benefit_assoc p " +
            " ON a.plan_id = p.plan_id WHERE p.plan_status != '"+ PlanStatus.WITHDRAWN.name()+"'";

    public static final String FIND_AGENT_CONTACT_QUERY = "SELECT agent_id AS agentId,email_id AS emailId,fax_number AS faxNumber,line_of_business AS lineOfBusiness,person_name AS personName,salutation,work_phone_number AS workPhoneNumber FROM `agent_contact_persons`";

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


    public static final String findPolicyCommissionByAgentId = "SELECT a.designation_code  agentCode," +
            " a.first_name                  agentFirstName, " +
            " a.last_name                   agentLastName, " +
            " tmtf.first_name               teamLeaderFirstName, " +
            " tmtf.last_name                teamLeaderLastName, " +
            " tmtf.employeeId               teamLeaderEmployeeId, " +
            " tmtf.branch_code              branchCode, " +
            " tmtf.region_code              regionCode, " +
            " tmbdf.first_name              bdeFirstName, " +
            " tmbdf.last_name               bdeLastName, " +
            " tmbdf.employeeId              bdeEmployeeId, " +
            " tmbmf.first_name              bmfFirstName, " +
            " tmbmf.last_name               bmfLastName, " +
            " tmbmf.employeeId              bmfEmployeeId, " +
            " tmrmf.first_name              rmfFirstName, " +
            " tmrmf.last_name               rmfLastName, " +
            " tmrmf.employeeId              rmfEmployeeId  " +
            " FROM agent a LEFT JOIN  " +
            " (SELECT t.team_id ,tf.employee_id employeeId, tf.first_name,tf.last_name,t.branch_code ,t.region_code,tf.thru_date ,tf.from_date " +
            " FROM team t INNER JOIN team_team_leader_fulfillment tf  " +
            " ON t.team_id=tf.team_id WHERE CASE WHEN tf.thru_date IS NULL THEN tf.from_date <= DATE(:currentDate) " +
            "          WHEN tf.thru_date IS NOT NULL THEN DATE(:currentDate) BETWEEN tf.from_date AND tf.thru_date  " +
            "          END) tmtf ON a.team_id=tmtf.team_id LEFT JOIN  " +
            " (SELECT bdf.employee_id employeeId ,bdf.first_name,bdf.last_name,bdf.branch_code,bdf.thru_date ,bdf.from_date FROM branch_bde_fulfillment bdf  " +
            " INNER JOIN branch b ON bdf.branch_code = b.branch_code WHERE    " +
            " CASE WHEN bdf.thru_date IS NULL THEN bdf.from_date <= DATE(:currentDate) " +
            "          WHEN bdf.thru_date IS NOT NULL THEN DATE(:currentDate) BETWEEN bdf.from_date AND bdf.thru_date  " +
            "          END ) tmbdf ON tmtf.branch_code = tmbdf.branch_code LEFT JOIN  " +
            " (SELECT bmf.employee_id employeeId,bmf.first_name,bmf.last_name ,bmf.branch_code,bmf.thru_date ,bmf.from_date FROM branch_manager_fulfillment bmf " +
            " INNER JOIN branch b ON bmf.branch_code = b.branch_code  " +
            " WHERE CASE WHEN bmf.thru_date IS NULL THEN bmf.from_date <= DATE(:currentDate) " +
            "          WHEN bmf.thru_date IS NOT NULL THEN DATE(:currentDate) BETWEEN bmf.from_date AND bmf.thru_date  " +
            "          END)  tmbmf ON tmbmf.branch_code = tmtf.branch_code   LEFT JOIN  " +
            " (SELECT rmf.employee_id employeeId ,rmf.first_name,rmf.last_name, rmf.region_code,rmf.thru_date ,rmf.from_date FROM region_manager_fulfillment rmf " +
            " INNER JOIN region r ON r.region_code=rmf.region_code WHERE   " +
            " CASE WHEN rmf.thru_date IS NULL THEN rmf.from_date <= DATE(:currentDate) " +
            "          WHEN rmf.thru_date IS NOT NULL THEN DATE(:currentDate) BETWEEN rmf.from_date AND rmf.thru_date  " +
            "          END) tmrmf ON tmrmf.region_code = tmtf.region_code " +
            " WHERE a.agent_id= :agentId ";

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
        Preconditions.checkArgument(isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
    }

    public List<Map<String, Object>> getAllAgentPlan() {
        return namedParameterJdbcTemplate.query(FIND_AGENT_PLAN, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllAgentContacts() {
        return namedParameterJdbcTemplate.query(FIND_AGENT_CONTACT_QUERY, new ColumnMapRowMapper());
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

    public List<Map<String ,Object>> findPolicyCommissionByAgentId(AgentId agentId, DateTime currentDate){
        return namedParameterJdbcTemplate.query(findPolicyCommissionByAgentId, new MapSqlParameterSource("agentId", agentId.getAgentId()).addValue("currentDate", currentDate), new ColumnMapRowMapper());
    }

}
