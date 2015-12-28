package com.pla.core.SBCM.query;

import com.pla.core.hcp.domain.model.HCPRate;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@Finder
@Component
public class SBCMFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private MongoTemplate mongoTemplate;
    public static final String FIND_COVERAGE_BY_ID_QUERY = "SELECT coverage_name AS coverageName FROM coverage WHERE coverage_id=:coverageId";

    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, Object> getCoverageDetail(String coverageId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_COVERAGE_BY_ID_QUERY, new MapSqlParameterSource().addValue("coverageId", coverageId));
    }

    public List<HCPRate> getAllServicesFromHCPRate() {
        Query query = new Query();
        query.fields().include("hcpServiceDetails");
        return mongoTemplate.find(query, HCPRate.class);
    }
}
