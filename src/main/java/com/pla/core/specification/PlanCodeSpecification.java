package com.pla.core.specification;

import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * For checking against duplicate Plan Code.
 *
 * @author: pradyumna
 * @since 1.0 22/03/2015
 */
@Specification
public class PlanCodeSpecification {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public boolean isSatisfiedBy(String planId, String planCode) {
        Number entries = namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM PLAN_ENTRY WHERE lower(PLAN_ID)" +
                        "<>:planId and lower(PLAN_CODE)=:planCode",
                new MapSqlParameterSource().addValue("planId", planId.toLowerCase()).addValue("planCode", planCode.toLowerCase()), Number.class);
        return entries.intValue() == 0;
    }

}
