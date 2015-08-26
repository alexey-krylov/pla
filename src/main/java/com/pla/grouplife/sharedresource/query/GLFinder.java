package com.pla.grouplife.sharedresource.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.pla.sharedkernel.identifier.QuotationId;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 6/24/2015.
 */

@Finder
@Service
public class GLFinder {

    private static final String GL_POLICY_COLLECTION_NAME = "group_life_policy";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private MongoTemplate mongoTemplate;

    public static final String FIND_OCCUPATION_CLASS_QUERY = "SELECT code,description FROM occupation_class WHERE description=:occupation";
    public static final String FIND_ACTIVE_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId AND agentStatus='ACTIVE'";

    public static final String FIND_INDUSTRY_BY_ID_QUERY = "SELECT industry_id AS industryId,industry_name AS industryName,risk_class AS riskClass,industry_factor AS industryFactor FROM industry where industry_id=:industryId";

    public static final String FIND_COVERAGE_BY_CODE_QUERY = "SELECT coverage_id AS coverageId, coverage_code AS coverageCode,coverage_name AS coverageName FROM coverage WHERE coverage_code=:coverageCode";
    public static final String FIND_ACTIVE_AGENT_BY_FIRST_NAME_QUERY = "SELECT * FROM agent_team_branch_view WHERE firstName =:firstName";
    public static final String FIND_AGENT_PLANS_QUERY = "SELECT agent_id as agentId,plan_id as planId FROM `agent_authorized_plan` WHERE agent_id=:agentId";

    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public String getOccupationClass(String occupation) {
        List<Map<String, Object>> occupationClassList = namedParameterJdbcTemplate.query(FIND_OCCUPATION_CLASS_QUERY, new MapSqlParameterSource().addValue("occupation", occupation), new ColumnMapRowMapper());
        if (isNotEmpty(occupationClassList)) {
            Map<String, Object> occupationClassMap = occupationClassList.get(0);
            return (String) occupationClassMap.get("code");
        }
        return "";
    }

    public List<Map> searchQuotation(String quotationNumber, String agentCode, String proposerName, String agentName, String quotationId, String[] statuses) {
        Criteria criteria = Criteria.where("quotationStatus").in(statuses);
        boolean areFieldsOtherThanAgentNameNotEmpty = false;
        if (isEmpty(quotationNumber) && isEmpty(quotationId) && isEmpty(agentCode) && isEmpty(proposerName) && isEmpty(agentName)) {
            return Lists.newArrayList();
        }
        if (isNotEmpty(quotationId)) {
            criteria = criteria.and("_id").is(new QuotationId(quotationId));
            areFieldsOtherThanAgentNameNotEmpty = true;
        }
        if (isNotEmpty(quotationNumber)) {
            criteria = criteria.and("quotationNumber").is(quotationNumber);
            areFieldsOtherThanAgentNameNotEmpty = true;
        }
        if (isNotEmpty(agentCode)) {
            criteria = criteria != null ? criteria.and("agentId.agentId").is(agentCode) : Criteria.where("agentId.agentId").is(agentCode);
            areFieldsOtherThanAgentNameNotEmpty = true;
        }
        if (isNotEmpty(proposerName)) {
            String proposerPattern = "^" + proposerName;
            criteria = criteria != null ? criteria.and("proposer.proposerName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE)) : Criteria.where("proposer.proposerName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE));
            areFieldsOtherThanAgentNameNotEmpty = true;
        }
        Set<String> agentIds = null;
        if (isNotEmpty(agentName)) {
            List<Map<String, Object>> agentList = namedParameterJdbcTemplate.queryForList(FIND_ACTIVE_AGENT_BY_FIRST_NAME_QUERY, new MapSqlParameterSource().addValue("firstName", agentName));
            agentIds = agentList.stream().map(new Function<Map<String, Object>, String>() {
                @Override
                public String apply(Map<String, Object> stringObjectMap) {
                    return (String) stringObjectMap.get("agentId");
                }
            }).collect(Collectors.toSet());
        }
        if (isNotEmpty(agentIds)) {
            criteria = criteria.and("agentId.agentId").in(agentIds);
        }
        if (isEmpty(agentIds) && !areFieldsOtherThanAgentNameNotEmpty) {
            return Lists.newArrayList();
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "quotationNumber"));
        query.with(new Sort(Sort.Direction.DESC, "versionNumber"));
        return mongoTemplate.find(query, Map.class, "group_life_quotation");
    }


    public Map getQuotationById(String quotationId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", quotationId);
        Map quotation = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_life_quotation");
        return quotation;
    }

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(isNotEmpty(agentId));
        List<Map<String, Object>> agentList = namedParameterJdbcTemplate.queryForList(FIND_ACTIVE_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
        return isNotEmpty(agentList) ? agentList.get(0) : Maps.newHashMap();
    }


    public Map<String, Object> findIndustryById(String industryId) {
        List<Map<String, Object>> industryList = namedParameterJdbcTemplate.queryForList(FIND_INDUSTRY_BY_ID_QUERY, new MapSqlParameterSource().addValue("industryId", industryId));
        return isNotEmpty(industryList) ? industryList.get(0) : null;
    }


    public Map<String, Object> findCoverageDetailByCoverageCode(String coverageCode) {
        return namedParameterJdbcTemplate.queryForMap(FIND_COVERAGE_BY_CODE_QUERY, new MapSqlParameterSource().addValue("coverageCode", coverageCode));
    }

    public Map searchQuotationById(QuotationId quotationId) {
        Criteria criteria = Criteria.where("_id").is(quotationId);
        Query query = new Query(criteria);
        Map quotationMap = mongoTemplate.findOne(query, Map.class, "group_life_quotation");
        return quotationMap;
    }

    public Map findProposalById(String proposalId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_life_proposal");
        return proposal;
    }

    public List<Map<String, Object>> getAgentAuthorizedPlan(String agentId) {
        return namedParameterJdbcTemplate.query(FIND_AGENT_PLANS_QUERY, new MapSqlParameterSource().addValue("agentId", agentId), new ColumnMapRowMapper());
    }


    public List<Map> searchPolicy(String policyNumber, String policyHolderName,String clientId, String[] statuses,String proposalNumber) {
        if (isEmpty(policyHolderName) && isEmpty(policyNumber)) {
            return Lists.newArrayList();
        }
        Criteria criteria = Criteria.where("status").in(statuses);
        if (isNotEmpty(policyHolderName)) {
            String proposerPattern = "^" + policyHolderName;
            criteria = criteria.and("proposer.proposerName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policyNumber.policyNumber").is(policyNumber);
        }
        if(isNotEmpty(proposalNumber)){
            criteria = criteria.and("proposal.proposalNumber.proposalNumber").is(proposalNumber);

        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "policyNumber.policyNumber"));
        return mongoTemplate.find(query, Map.class, GL_POLICY_COLLECTION_NAME);
    }
}
