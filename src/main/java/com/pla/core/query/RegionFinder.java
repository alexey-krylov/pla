/*
 * Copyright (c) 3/5/15 4:08 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author: Nischitha
 * @since 1.0 30/03/2015
 */
@Finder
@Service
public class RegionFinder {

    public static final String FIND_REGION_BY_ID_QUERY = "select * from region_region_manger_fulfilment_view where regionCode=:regionId";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map<String, Object> getRegionById(String regionId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_REGION_BY_ID_QUERY, new MapSqlParameterSource().addValue("regionId", regionId));
    }
}



