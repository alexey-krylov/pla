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

    public static final String FIND_REGION_BY_ID_QUERY = "select * from region_region_manger_fulfilment_view where regionCode=:regionId";
    public static final String FIND_ALL_ACTIVE_REGIONAL_MANAGERS_QUERY = "SELECT R.region_code AS regionCode," +
            "     R.region_name AS regionName," +
            "     RF.first_name AS regionalManagerFirstName," +
            "     RF.last_name  AS regionalManagerLastName," +
            "     RF.from_date   AS regionalManagerFromDate," +
            "     RF.employee_id AS currentRegionalManager " +
            "     FROM region R LEFT JOIN `region_manager_fulfillment` RF " +
            "     ON R.region_code = RF.region_code AND ((RF.thru_date IS NULL) OR (RF.thru_date >= DATE(CURDATE()+1)))";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map<String, Object> getRegionById(String regionId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_REGION_BY_ID_QUERY, new MapSqlParameterSource().addValue("regionId", regionId));
    }

    public List<Map<String, Object>> getAllActiveRegionalManagers() {
        return namedParameterJdbcTemplate.query(FIND_ALL_ACTIVE_REGIONAL_MANAGERS_QUERY, new ColumnMapRowMapper());

    }
}



