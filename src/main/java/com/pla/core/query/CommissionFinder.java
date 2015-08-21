/*
 * Copyright (c) 3/5/15 4:08 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.pla.sharedkernel.domain.model.CommissionType;
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
 * @since 1.0 05/03/2015
 */
@Finder
@Service
public class CommissionFinder {

    public static final String FIND_COMMISSION_BY_ID_QUERY = "select * from commission_view  where commissionId=:commissionId";

    public static final String FIND_COMMISSION_TERM_BY_COMMISSION_ID_QUERY = "select * from commission_commission_term_view  where commissionId=:commissionId";

    public static final String FIND_ALL_COMMISSION_BY_TYPE_QUERY = "select * from commission_view where commissionType=:commissiontype";

    public static final String FIND_ALL_COMMISSION_TERM_QUERY = "select * from commission_commission_term_view";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map<String, Object> getCommissionById(String commissionId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_COMMISSION_BY_ID_QUERY, new MapSqlParameterSource().addValue("commissionId", commissionId));
    }

    public List<Map<String, Object>> getCommissionTermByCommissionId(String commissionId) {
        return namedParameterJdbcTemplate.query(FIND_COMMISSION_TERM_BY_COMMISSION_ID_QUERY, new MapSqlParameterSource().addValue("commissionId", commissionId), new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllCommissionByCommissionType(CommissionType commissionType) {
        return namedParameterJdbcTemplate.query(FIND_ALL_COMMISSION_BY_TYPE_QUERY, new MapSqlParameterSource().addValue("commissiontype", commissionType.toString()), new ColumnMapRowMapper());
    }

    public List<Map<String, Object>> getAllCommissionTerm() {
        return namedParameterJdbcTemplate.query(FIND_ALL_COMMISSION_TERM_QUERY, new ColumnMapRowMapper());
    }
}
