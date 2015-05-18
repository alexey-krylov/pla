package com.pla.external.listener;

import com.pla.core.domain.event.PlanCoverageAssociationEvent;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * It main job to store the relationship of Plan, Coverage and Benefits into Relational Database.
 * This will in turn populate the view which would be used for querying Coverages, Benefits
 * that are configured for the plan.
 * <p>
 * Created by pradyumna on 21-04-2015.
 */
@Component
public class PlanCoverageAssocListener {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @EventHandler
    public void handle(PlanCoverageAssociationEvent event) {
        Map<CoverageType, Map<CoverageId, List<BenefitId>>> payload = event.getCoverageAndBenefits();
        PlanId planId = event.getPlanId();
        namedParameterJdbcTemplate.execute("delete from plan_coverage_benefits_assoc",
                new EmptySqlParameterSource(), new PreparedStatementCallback<Object>() {
                    @Override
                    public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                        return ps.execute();
                    }
                });
        Map<CoverageId, List<BenefitId>> optionalCoverageBenefits = payload.get(CoverageType.OPTIONAL);
        populateOptionalCoverages(planId, event.getPlanName(), event.getPlanCode(), optionalCoverageBenefits);

        Map<CoverageId, List<BenefitId>> baseCoverageBenefits = payload.get(CoverageType.BASE);
        if (baseCoverageBenefits != null) populateBaseCoverages(planId, event.getPlanName(), event.getPlanCode(), baseCoverageBenefits);
    }

    private void populateOptionalCoverages(PlanId planId, String planName, String planCode, Map<CoverageId, List<BenefitId>> optionalCoverageBenefits) {
        if (optionalCoverageBenefits == null) return;
        for (CoverageId coverageId : optionalCoverageBenefits.keySet()) {
            for (BenefitId benefitId : optionalCoverageBenefits.get(coverageId)) {
                MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                        .addValue("planId", planId)
                        .addValue("coverageId", coverageId.toString())
                        .addValue("benefitId", benefitId.toString())
                        .addValue("planName", planName)
                        .addValue("planCode", planCode);
                namedParameterJdbcTemplate.execute("insert into plan_coverage_benefits_assoc (`plan_id`,`plan_name`,`plan_code`,`coverage_id`," +
                                "`benefit_id`,`optional`) values (:planId,:planName,:planCode,:coverageId,:benefitId,0)", parameterSource,
                        new PreparedStatementCallback<Object>() {
                            @Override
                            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                                return ps.execute();
                            }
                        });
            }
        }
    }

    private void populateBaseCoverages(PlanId planId, String planName, String planCode, Map<CoverageId, List<BenefitId>> baseCoverageBenefits) {
        if (baseCoverageBenefits == null) return;

        for (CoverageId coverageId : baseCoverageBenefits.keySet()) {
            for (BenefitId benefitId : baseCoverageBenefits.get(coverageId)) {
                MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                        .addValue("planId", planId.toString())
                        .addValue("coverageId", coverageId.toString())
                        .addValue("benefitId", benefitId.toString())
                        .addValue("planName", planName)
                        .addValue("planCode", planCode);
                namedParameterJdbcTemplate.execute("insert into plan_coverage_benefits_assoc (`plan_id`,`plan_name`,`plan_code`,`coverage_id`," +
                                "`benefit_id`,`optional`) values (:planId,:planName,:planCode,:coverageId,:benefitId,1)", parameterSource,
                        new PreparedStatementCallback<Object>() {
                            @Override
                            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                                return ps.execute();
                            }
                        });
            }
        }
    }
}
