package com.pla.individuallife.quotation.query;

import com.google.common.base.Preconditions;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Finder
@Service
public class ILQuotationFinder {

    public static final String FIND_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId";
    private static final String IL_QUOTATION_TABLE = "individual_life_quotation";
    public static final String FIND_QUOTATION_BY_ID_QUERY = "select * from " + IL_QUOTATION_TABLE + " where quotation_id =:quotationId";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
    }

    public ILQuotationDto getQuotationById(String quotationId) {
        return namedParameterJdbcTemplate.queryForObject(FIND_QUOTATION_BY_ID_QUERY, new MapSqlParameterSource().addValue("quotationId", quotationId), new BeanPropertyRowMapper<ILQuotationDto>(ILQuotationDto.class));
    }

    public List<Map> getAllQuotation() {
        return mongoTemplate.findAll(Map.class, "individual_life_quotation");
    }

    public List<Map> searchQuotation(String quotationNumber, String agentCode, String proposerName) {
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
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, "individual_life_quotation");
    }

}
