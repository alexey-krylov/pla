package com.pla.core.query;

import com.google.common.base.Preconditions;
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

    public static final String ACTIVE_COVERAGE_COUNT_BY_COVERAGE_NAME_QUERY = "select count(coverage_id) from coverage where coverage_name=:coverageName and status in ('ACTIVE','INUSE')";

    public static final String ACTIVE_COVERAGE_COUNT_BY_COVERAGE_CODE_QUERY = "select count(coverage_id) from coverage where coverage_code=:coverageCode and  status in ('ACTIVE','INUSE')";

    public static final String FIND_ALL_COVERAGE_QUERY = "SELECT c.coverage_id coverageId,c.coverage_code coverageCode,c.coverage_name coverageName,c.description description ,c.status AS coverageStatus " +
            "FROM coverage c WHERE c.status='ACTIVE'";

    public static final String FIND_ALL_BENEFITS_ASSOCIATED_WITH_THE_COVERAGE_QUERY = "select b.benefit_id benefitId,b.benefit_name benefitName from  benefit b  " +
            "inner join coverage_benefit cb on b.benefit_id=cb.benefit_id inner join coverage c on cb.coverage_id=c.coverage_id " +
            "where c.status='ACTIVE' and b.status='ACTIVE' and c.coverage_id=:coverageId ORDER BY b.benefit_name ASC ";

    public int getCoverageCountByCoverageName(String coverageName){
        Preconditions.checkNotNull(coverageName);
        Number noOfCoverages = namedParameterJdbcTemplate.queryForObject(ACTIVE_COVERAGE_COUNT_BY_COVERAGE_NAME_QUERY, new MapSqlParameterSource().addValue("coverageName", coverageName), Number.class);
        return noOfCoverages.intValue();
    }

    public int getCoverageCountByCoverageCode(String coverageCode){
        Preconditions.checkNotNull(coverageCode);
        Number noOfCoverages = namedParameterJdbcTemplate.queryForObject(ACTIVE_COVERAGE_COUNT_BY_COVERAGE_CODE_QUERY, new MapSqlParameterSource().addValue("coverageCode", coverageCode), Number.class);
        return noOfCoverages.intValue();
    }


    public List<CoverageDto> getAllCoverage() {
        List<CoverageDto> listOfActiveCoverage  = namedParameterJdbcTemplate.query(FIND_ALL_COVERAGE_QUERY, new BeanPropertyRowMapper(CoverageDto.class));
        for (CoverageDto coverageDto : listOfActiveCoverage){
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource("coverageId",coverageDto.getCoverageId());
            List<Map<String,Object>> listOfBenefits = namedParameterJdbcTemplate.query(FIND_ALL_BENEFITS_ASSOCIATED_WITH_THE_COVERAGE_QUERY, sqlParameterSource, new ColumnMapRowMapper());
            coverageDto.setBenefitDtos(listOfBenefits);
        }
        return listOfActiveCoverage;
    }

}

