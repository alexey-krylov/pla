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

    public static final String getPlanDetailsByLineOfBusinessQuery = "SELECT DISTINCT p.plan_code planCode, p.plan_name planName ,p.plan_id planId FROM " +
            " plan_coverage_benefit_assoc p " +
            " WHERE p.line_of_business = :lineOfBusiness  ";


    public static final String getAllProductClaimMappingDetailsQuery = " SELECT * FROM product_claim_map_view ";

    public static final String getProductClaimMapDetailByProductClaimIdQuery = " SELECT * FROM product_claim_map_view WHERE productClaimId=:productClaimId ";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Map<String,Object>> getPlanDetailBy(LineOfBusinessEnum lineOfBusinessEnum){
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("lineOfBusiness",lineOfBusinessEnum.toString());
        return namedParameterJdbcTemplate.query(getPlanDetailsByLineOfBusinessQuery, sqlParameterSource, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllProductClaimMapDetail() {
        List<Map<String, Object>> productClaimDetail =  namedParameterJdbcTemplate.query(getAllProductClaimMappingDetailsQuery, new ColumnMapRowMapper());
        return productClaimDetail;
    }

    public List<Map<String, Object>> getProductClaimMapDetailById(String productClaimId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("productClaimId",productClaimId);
        List<Map<String, Object>> productClaimDetail =  namedParameterJdbcTemplate.query(getProductClaimMapDetailByProductClaimIdQuery, sqlParameterSource, new ColumnMapRowMapper());
        return productClaimDetail;
    }

}
