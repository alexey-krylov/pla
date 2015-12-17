package com.pla.core.hcp.query;

import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
@Finder
@Component
public class HCPFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
}
