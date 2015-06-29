package com.pla.grouplife.proposal.query;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
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

    public static final String FIND_ACTIVE_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId AND agentStatus='ACTIVE'";

    public Map<String, Object> getAgentById(String agentId) {
        checkArgument(isNotEmpty(agentId));
        List<Map<String, Object>> agentList = namedParameterJdbcTemplate.queryForList(FIND_ACTIVE_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
        return isNotEmpty(agentList) ? agentList.get(0) : Maps.newHashMap();
    }

    public Map getProposalForQuotation(QuotationId quotationId){
        checkArgument(quotationId != null);
        BasicDBObject query = new BasicDBObject();
        query.put("quotationId",quotationId);
        Map groupLifeProposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_life_proposal");
        return  groupLifeProposal;
    }

    public Map getProposalById(ProposalId proposalId){
        checkArgument(proposalId != null);
        BasicDBObject query = new BasicDBObject();
        query.put("_id",proposalId);
        Map groupLifeProposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_life_proposal");
        return  groupLifeProposal;
    }

    public List<Map<String, Object>> getAgentAuthorizedPlan(String agentId) {
        return null;
    }
}
