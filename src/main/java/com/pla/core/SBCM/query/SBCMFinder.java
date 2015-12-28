package com.pla.core.SBCM.query;

import com.pla.core.hcp.domain.model.HCPRate;
import org.apache.commons.lang.StringUtils;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@Finder
@Component
public class SBCMFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private MongoTemplate mongoTemplate;

    public static final String FIND_COVERAGE_BY_ID_QUERY = "SELECT coverage_name AS coverageName FROM coverage WHERE coverage_id=:coverageId";
    public static final String FIND_BENEFIT_BY_ID_QUERY = "SELECT benefit_name AS benefitName FROM benefit WHERE benefit_id=:benefitId";

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

    public String getBenefitNameByBenefitId(String benefitId) {
        List<Map<String, Object>> benefits = namedParameterJdbcTemplate.queryForList(FIND_BENEFIT_BY_ID_QUERY, new MapSqlParameterSource("benefitId", benefitId));
        if(isNotEmpty(benefits)){
            Map<String, Object> benefit = benefits.get(0);
            return benefit.get("benefitName").toString();
        }
        return StringUtils.EMPTY;
    }

    public String getCoverageNameByCoverageId(String coverageId) {
        List<Map<String, Object>> coverages = namedParameterJdbcTemplate.queryForList(FIND_COVERAGE_BY_ID_QUERY, new MapSqlParameterSource().addValue("coverageId", coverageId));
        if(isNotEmpty(coverages)){
            Map<String, Object> coverage = coverages.get(0);
            return coverage.get("coverageName").toString();
        }
        return StringUtils.EMPTY;
    }
}
