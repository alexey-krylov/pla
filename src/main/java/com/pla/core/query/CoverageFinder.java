package com.pla.core.query;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.pla.core.dto.BenefitDto;
import com.pla.core.dto.CoverageDto;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 3/23/15
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */
@Finder
@Service
public class CoverageFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public static final String activeCoverageCountByCoverageNameQuery = "select count(coverage_id) from coverage where coverage_name=:coverageName and status='ACTIVE'";

    public static final String findAllCoverageQuery = "SELECT c.coverage_id coverageId,c.coverage_name coverageName,c.description description ,c.status AS coverageStatus " +
            "FROM coverage c WHERE c.status='ACTIVE'";

    public static final String findAllBenefitsAssociatedWithTheCoverageQuery = "select b.benefit_id benefitId,b.benefit_name benefitName from  benefit b  " +
            "inner join coverage_benefit cb on b.benefit_id=cb.benefit_id inner join coverage c on cb.coverage_id=c.coverage_id " +
            "where c.status='ACTIVE' and b.status='ACTIVE' and c.coverage_id=:coverageId";

    public int getCoverageCountByCoverageName(String coverageName){
        Preconditions.checkNotNull(coverageName);
        Number noOfBenefit = namedParameterJdbcTemplate.queryForObject(activeCoverageCountByCoverageNameQuery, new MapSqlParameterSource().addValue("coverageName", coverageName), Number.class);
        return noOfBenefit.intValue();
    }

    public List<CoverageDto> getAllCoverage() {
        List<CoverageDto> listOfActiveCoverage  = namedParameterJdbcTemplate.query(findAllCoverageQuery, new BeanPropertyRowMapper(CoverageDto.class));
        for (CoverageDto coverageDto : listOfActiveCoverage){
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource("coverageId",coverageDto.getCoverageId());
            coverageDto.benefitDtos  = namedParameterJdbcTemplate.query(findAllBenefitsAssociatedWithTheCoverageQuery, sqlParameterSource,new BeanPropertyRowMapper(BenefitDto.class));
        }
        return listOfActiveCoverage;
    }

}

