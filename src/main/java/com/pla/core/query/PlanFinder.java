package com.pla.core.query;

import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */
@Finder
@Service
public class PlanFinder {
    public static final String FIND_ALL_PLANS = "select * from plan_entry";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> findAllPlans() {
        return namedParameterJdbcTemplate.query(FIND_ALL_PLANS, new ColumnMapRowMapper());
    }
}
