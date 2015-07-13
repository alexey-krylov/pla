package com.pla.grouplife.proposal.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.pla.sharedkernel.identifier.ProposalId;
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

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 6/24/2015.
 */
@Service
@Finder
public class GLProposalFinder {


    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private MongoTemplate mongoTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public static final String FIND_AGENT_PLANS_QUERY = "SELECT agent_id as agentId,plan_id as planId FROM `agent_authorized_plan` WHERE agent_id=:agentId";

    public static final String FIND_ACTIVE_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId AND agentStatus='ACTIVE'";

    public static final String FIND_ACTIVE_AGENT_BY_FIRST_NAME_QUERY = "SELECT * FROM agent_team_branch_view WHERE firstName =:firstName";

    public Map<String, Object> getAgentById(String agentId) {
        checkArgument(isNotEmpty(agentId));
        List<Map<String, Object>> agentList = namedParameterJdbcTemplate.queryForList(FIND_ACTIVE_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
        return isNotEmpty(agentList) ? agentList.get(0) : Maps.newHashMap();
    }

    public Map getProposalForQuotation(QuotationId quotationId) {
        checkArgument(quotationId != null);
        BasicDBObject query = new BasicDBObject();
        query.put("quotationId", quotationId);
        Map groupLifeProposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_life_proposal");
        return groupLifeProposal;
    }

    public Map getProposalById(ProposalId proposalId) {
        checkArgument(proposalId != null);
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map groupLifeProposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_life_proposal");
        return groupLifeProposal;
    }

    public List<Map<String, Object>> getAgentAuthorizedPlan(String agentId) {
        return namedParameterJdbcTemplate.query(FIND_AGENT_PLANS_QUERY, new MapSqlParameterSource().addValue("agentId", agentId), new ColumnMapRowMapper());
    }

    public Map findProposalByQuotationNumber(String quotationNumber) {
        Criteria criteria = Criteria.where("quotation.quotationNumber").is(quotationNumber);
        Query query = new Query(criteria);
        Map proposalMap = mongoTemplate.findOne(query, Map.class, "group_life_proposal");
        return proposalMap;
    }

    public List<Map> searchProposal(String proposalNumber, String proposerName, String agentName, String agentCode, String proposalId, String[] statuses) {
        Criteria criteria = Criteria.where("proposalStatus").in(statuses);
        if (isEmpty(proposalNumber) && isEmpty(proposalId) && isEmpty(agentCode) && isEmpty(proposerName) && isEmpty(agentName)) {
            return Lists.newArrayList();
        }
        if (isNotEmpty(proposalId)) {
            criteria = criteria.and("_id").is(new ProposalId(proposalId));
        }
        if (isNotEmpty(proposalNumber)) {
            criteria = criteria.and("proposalNumber.proposalNumber").is(proposalNumber);
        }
        if (isNotEmpty(agentCode)) {
            criteria = criteria != null ? criteria.and("agentId.agentId").is(agentCode) : Criteria.where("agentId.agentId").is(agentCode);
        }
        if (isNotEmpty(proposerName)) {
            String proposerPattern = "^" + proposerName;
            criteria = criteria != null ? criteria.and("proposer.proposerName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE)) : Criteria.where("proposer.proposerName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE));
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
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "proposalNumber.proposalNumber"));
        return mongoTemplate.find(query, Map.class, "group_life_proposal");
    }

}
