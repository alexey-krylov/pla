package com.pla.core.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 3/30/2015.
 */
public class BranchFinder {

    public static final String FIND_ALL_BRANCH_QUERY = "SELECT br.branch_name AS branchName,bmf.first_name AS branchManagerFirstName, bmf.last_name AS branchManagerLastName, bmf.from_date AS branchManagerFromDate, bbf.first_name AS firstName, bbf.last_name AS branchBDELastName, bbf.from_date AS branchBDEFromDate " +
            " FROM branch br " +
            "LEFT JOIN branch_manager_fulfillment bmf ON bmf.employee_id = br.current_branch_manager " +
            "LEFT JOIN branch_bde_fulfillment bbf ON bbf.employee_id = br.current_branch_bde";
    public static final String FIND_BRANCH_BY_ID_QUERY = "SELECT bmf.first_name AS firstName, bmf.last_name AS branchManagerLastName, bmf.from_date AS branchManagerFromDate, bbf.first_name AS firstName, bbf.last_name AS branchBDELastName, bbf.from_date AS branchBDEFromDate  " +
            " FROM branch br " +
            "LEFT JOIN branch_manager_fulfillment bmf ON bmf.employee_id = br.current_branch_manager " +
            "LEFT JOIN branch_bde_fulfillment bbf ON bbf.employee_id = br.current_branch_bde " +
            "WHERE br.branch_code=:branchId";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> getAllBranch() {
        return namedParameterJdbcTemplate.query(FIND_ALL_BRANCH_QUERY, new ColumnMapRowMapper());
    }

    public Map<String, Object> getBranchById(String branchId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_BRANCH_BY_ID_QUERY, new MapSqlParameterSource().addValue("branchId", branchId));
    }
}
