package com.pla.core.SBCM.query;

import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@Finder
@Component
public class SBCMFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public static final String FIND_COVERAGE_BY_ID_QUERY = "SELECT coverage_name AS coverageName FROM coverage WHERE coverage_id=:coverageId";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map<String, Object> getCoverageDetail(String coverageId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_COVERAGE_BY_ID_QUERY, new MapSqlParameterSource().addValue("coverageId", coverageId));
    }
}
