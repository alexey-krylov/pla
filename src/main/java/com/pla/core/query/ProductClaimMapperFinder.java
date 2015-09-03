package com.pla.core.query;

import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 9/2/2015.
 */
@Finder
@Service
public class ProductClaimMapperFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static final String getPlanDetailsByLineOfBusiness = " SELECT DISTINCT p.plan_code planCode,p.plan_id planId, p.plan_name planName,c.coverage_id coverageId,c.coverage_name coverageName FROM " +
            " plan_coverage_benefit_assoc p INNER JOIN coverage c ON p.coverage_id = c.coverage_id " +
            " WHERE p.line_of_business=  :lineOfBusiness  ";


    public static final String getAllProductClaimMappingDetails = " SELECT * FROM product_claim_map_view ";

    public static final String getProductClaimMapDetailByProductClaimId = " SELECT * FROM product_claim_map_view WHERE productClaimId=:productClaimId ";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<Map<String,Object>> getPlanDetailBy(LineOfBusinessEnum lineOfBusinessEnum){
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("lineOfBusiness",lineOfBusinessEnum.toString());
        return namedParameterJdbcTemplate.query(getPlanDetailsByLineOfBusiness, sqlParameterSource, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllProductClaimMapDetail() {
        List<Map<String, Object>> productClaimDetail =  namedParameterJdbcTemplate.query(getAllProductClaimMappingDetails, new ColumnMapRowMapper());
        return productClaimDetail;
    }

    public List<Map<String, Object>> getProductClaimMapDetailById(String productClaimId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("productClaimId",productClaimId);
        List<Map<String, Object>> productClaimDetail =  namedParameterJdbcTemplate.query(getProductClaimMapDetailByProductClaimId, sqlParameterSource, new ColumnMapRowMapper());
        return productClaimDetail;
    }

}
