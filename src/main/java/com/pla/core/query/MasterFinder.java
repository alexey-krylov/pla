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

    public static final String FIND_GEO_BY_GEO_TYPE_QUERY = "SELECT geo_id AS geoId,parent_geo_id AS parentGeoId, geo_type AS geoType, geo_description AS geoName FROM geo WHERE geo_type=:geoType";

    public static final String FIND_ALL_CHANNEL_TYPE_QUERY = "SELECT channel_code AS channelCode ,channel_description AS channelDescription FROM channel_type";

    public static final String FIND_ALL_REGION_QUERY = "select * from region_region_manger_fulfilment_view";

    public static final String FIND_ALL_REGION_REGION_FULFILLMENT_GREATER_THAN_CURRENT_DATE_QUERY = "select * from region_region_manger_fulfilment_greater_than_current_date_view";

    public static final String FINA_ALL_BRANCH_QUERY = "SELECT B.branch_code AS branch_code,B.branch_name AS branchName FROM `region_branch` RB LEFT JOIN  `branch` B ON RB.branch_code =B.branch_code\n" +
            "  WHERE RB.region_code = :regionCode";

    public static final String FIND_ENTITY_SEQUENCE_BY_CLASS_TYPE_QUERY = "SELECT sequence_id as sequenceId, sequence_number AS sequenceNumber,sequence_name AS sequenceName,sequence_prefix AS sequencePrefix FROM `entity_sequence` WHERE sequence_name=:sequenceName";

    public static final String FIND_ALL_DOCUMENT = "SELECT document_code documentCode,document_name documentName " +
            " FROM document where is_provided = 'NO'";

    public static final String FIND_ALL_INDUSTRY_QUERY = "SELECT * FROM industry";

    public static final String FIND_ALL_OCCUPATION_CLASS_QUERY = "SELECT DISTINCT(CODE) FROM occupation_class";

    public static final String FIND_ALL_OCCUPATION_CLASSIFICATION_QUERY = "SELECT DISTINCT(description) FROM occupation_class";

    public static final String FIND_ALL_DESIGNATION_QUERY = "SELECT * FROM `designation`";

    public List<Map<String, Object>> getGeoByGeoType(GeoType geoType) {
        return namedParameterJdbcTemplate.query(FIND_GEO_BY_GEO_TYPE_QUERY, new MapSqlParameterSource().addValue("geoType", geoType.name()), new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllChanelType() {
        return namedParameterJdbcTemplate.query(FIND_ALL_CHANNEL_TYPE_QUERY, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllRegion() {
        return namedParameterJdbcTemplate.query(FIND_ALL_REGION_QUERY, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllRegionGreaterThanCurrentDate() {
        return namedParameterJdbcTemplate.query(FIND_ALL_REGION_REGION_FULFILLMENT_GREATER_THAN_CURRENT_DATE_QUERY, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getBranchByRegion(String regionCode) {
        return namedParameterJdbcTemplate.query(FINA_ALL_BRANCH_QUERY, new MapSqlParameterSource().addValue("regionCode", regionCode), new ColumnMapRowMapper());
    }

    public Map<String, Object> getEntitySequenceFor(Class clazz) {
        return namedParameterJdbcTemplate.queryForMap(FIND_ENTITY_SEQUENCE_BY_CLASS_TYPE_QUERY, new MapSqlParameterSource().addValue("sequenceName", clazz.getName()));
    }

    public List<Map<String, Object>> getAllDocument() {
        return namedParameterJdbcTemplate.query(FIND_ALL_DOCUMENT, new ColumnMapRowMapper());
    }


    public List<Map<String, Object>> getAllIndustry() {
        return namedParameterJdbcTemplate.query(FIND_ALL_INDUSTRY_QUERY, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllOccupationClass() {
        return namedParameterJdbcTemplate.query(FIND_ALL_OCCUPATION_CLASS_QUERY, new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllOccupationClassification() {
        return namedParameterJdbcTemplate.query(FIND_ALL_OCCUPATION_CLASSIFICATION_QUERY, new ColumnMapRowMapper());
    }


    public List<Map<String, Object>> getAllDesignation() {
        return namedParameterJdbcTemplate.query(FIND_ALL_DESIGNATION_QUERY, new ColumnMapRowMapper());
    }
}
