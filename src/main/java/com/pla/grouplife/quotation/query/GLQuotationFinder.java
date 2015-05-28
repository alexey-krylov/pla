package com.pla.grouplife.quotation.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/14/2015.
 */
@Finder
@Service
public class GLQuotationFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private MongoTemplate mongoTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public static final String FIND_ACTIVE_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId AND agentStatus='ACTIVE'";

    public static final String FIND_AGENT_PLANS_QUERY = "SELECT agent_id as agentId,plan_id as planId FROM `agent_authorized_plan` WHERE agent_id=:agentId";

    public static final String FIND_OCCUPATION_CLASS_QUERY = "SELECT code,description FROM occupation_class WHERE description=:occupation";

    public static final String FIND_COVERAGE_BY_ID = "SELECT coverage_code AS coverageCode,coverage_name AS coverageName FROM coverage WHERE coverage_id=:coverageId";

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_ACTIVE_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
    }

    public Map getQuotationById(String quotationId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", quotationId);
        Map quotation = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_life_quotation");
        return quotation;
    }

    public List<Map> getAllQuotation() {
        return mongoTemplate.findAll(Map.class, "group_life_quotation");
    }

    public List<Map> searchQuotation(String quotationNumber, String agentCode, String proposerName, String agentName) {
        Criteria criteria = null;
        if (isNotEmpty(quotationNumber)) {
            criteria = Criteria.where("quotationNumber").is(quotationNumber);
        }
        if (isNotEmpty(agentCode)) {
            criteria = criteria != null ? criteria.and("agentId.agentId").is(agentCode) : Criteria.where("agentId.agentId").is(agentCode);
        }
        if (isNotEmpty(proposerName)) {
            criteria = criteria != null ? criteria.and("proposer.proposerName").is(proposerName) : Criteria.where("proposer.proposerName").is(proposerName);
        }
        if (criteria == null && isEmpty(agentName)) {
            return Lists.newArrayList();
        } else if (isNotEmpty(agentName)) {

        }
        Query query = new Query(criteria);
        List quotationList = mongoTemplate.find(query, Map.class, "group_life_quotation");

        return mongoTemplate.find(query, Map.class, "group_life_quotation");
    }

    public List<Map<String, Object>> getAgentAuthorizedPlan(String agentId) {
        return namedParameterJdbcTemplate.query(FIND_AGENT_PLANS_QUERY, new MapSqlParameterSource().addValue("agentId", agentId), new ColumnMapRowMapper());
    }

    public String getOccupationClass(String occupation) {
        List<Map<String, Object>> occupationClassList = namedParameterJdbcTemplate.query(FIND_OCCUPATION_CLASS_QUERY, new MapSqlParameterSource().addValue("occupation", occupation), new ColumnMapRowMapper());
        if (isNotEmpty(occupationClassList)) {
            Map<String, Object> occupationClassMap = occupationClassList.get(0);
            return (String) occupationClassMap.get("code");
        }
        return "";
    }

    public Map<String, Object> getCoverageDetail(String coverageId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_COVERAGE_BY_ID, new MapSqlParameterSource().addValue("coverageId", coverageId));
    }
}
