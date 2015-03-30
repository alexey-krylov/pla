/*
 * Copyright (c) 3/5/15 4:08 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @author: Nischitha
 * @since 1.0 30/03/2015
 */
@Finder
@Service
public class RegionFinder {

    public static final String FIND_ALL_REGION_QUERY = "SELECT region_name AS regionName,rmf.first_name AS firstName, rmf.last_name AS lastName, rmf.from_date AS fromDate FROM region rg " +
            "LEFT JOIN region_manager_fulfillment rmf ON rmf.employee_id = rg.regional_manager";
    public static final String FIND_REGION_BY_ID_QUERY = "SELECT rmf.first_name AS firstName, rmf.last_name AS lastName, rmf.from_date AS fromDate FROM region rg " +
            "LEFT JOIN region_manager_fulfillment rmf ON rmf.employee_id = rg.regional_manager " +
            "WHERE rg.region_code=:regionId";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> getAllRegion() {
        return namedParameterJdbcTemplate.query(FIND_ALL_REGION_QUERY, new ColumnMapRowMapper());
    }

    public Map<String, Object> getRegionById(String regionId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_REGION_BY_ID_QUERY, new MapSqlParameterSource().addValue("regionId", regionId));
    }
}



