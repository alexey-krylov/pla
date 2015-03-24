/*
 * Copyright (c) 3/20/15 9:20 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.pla.sharedkernel.domain.model.GeoType;
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
 * @author: Samir
 * @since 1.0 20/03/2015
 */
@Finder
@Service
public class MasterFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static final String FIND_GEO_BY_GEO_TYPE = "SELECT geo_id AS geoId,parent_geo_id AS parentGeoId, geo_type AS geoType, geo_description AS geoName FROM geo WHERE geo_type=:geoType";

    public static final String FIND_ALL_CHANNEL_TYPE = "SELECT channel_code AS channelCode ,channel_description AS channelDescription FROM channel_type";


    public static final String FIND_ALL_REGION = "select region_code AS regionCode, region_name AS regionName from region";

    public static final String FINA_ALL_BRANCH = "select BRANCH_CODE AS branchCode,BRANCH AS branchName from branch where region_code=:regionCode";

    public List<Map<String, Object>> getGeoByGeoType(GeoType geoType) {
        return namedParameterJdbcTemplate.query(FIND_GEO_BY_GEO_TYPE, new MapSqlParameterSource().addValue("geoType", geoType.name()), new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllChanelType() {
        return namedParameterJdbcTemplate.query(FIND_ALL_CHANNEL_TYPE, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllRegion() {
        return namedParameterJdbcTemplate.query(FIND_ALL_REGION, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getBranchByRegion(String regionCode) {
        return namedParameterJdbcTemplate.query(FINA_ALL_BRANCH, new MapSqlParameterSource().addValue("regionCode", regionCode), new ColumnMapRowMapper());
    }

}
