package com.pla.quotation.query;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
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

    public static final String FIND_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId";

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
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

    public List<Map> searchQuotation(String quotationNumber, String agentCode, String proposerName) {
        BasicDBObject query = new BasicDBObject();
        if (isNotEmpty(quotationNumber)) {
            query.put("quotationNumber", quotationNumber);
        } else if (isNotEmpty(agentCode)) {
            query.put("agentId.agentId", agentCode);
        } else if (isNotEmpty(proposerName)) {
            query.put("proposer.proposerName", proposerName);
        }
        return mongoTemplate.findAll(Map.class, "group_life_quotation");
    }

}
