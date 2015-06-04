package com.pla.external.listener;

import com.pla.core.domain.event.PlanCoverageAssociationEvent;
import com.pla.core.domain.event.PlanLaunchEvent;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.domain.model.PlanStatus;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Date;
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
    public void handle(final PlanCoverageAssociationEvent event) {
        Map<CoverageType, Map<CoverageId, List<BenefitId>>> payload = event.getCoverageAndBenefits();
        namedParameterJdbcTemplate.execute("delete from plan_coverage_benefits_assoc where plan_id="+"'"+ event.getPlanId().toString()+"'",
                new EmptySqlParameterSource(), new PreparedStatementCallback<Object>() {
                    @Override
                    public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                        return ps.execute();
                    }
                });
        Map<CoverageId, List<BenefitId>> optionalCoverageBenefits = payload.get(CoverageType.OPTIONAL);
        populateOptionalCoverages(event, optionalCoverageBenefits);

        Map<CoverageId, List<BenefitId>> baseCoverageBenefits = payload.get(CoverageType.BASE);
        if (baseCoverageBenefits != null) populateBaseCoverages(event, baseCoverageBenefits);
    }

    private void populateOptionalCoverages(PlanCoverageAssociationEvent event, Map<CoverageId, List<BenefitId>> optionalCoverageBenefits) {
        if (optionalCoverageBenefits == null) return;
        for (CoverageId coverageId : optionalCoverageBenefits.keySet()) {
            for (BenefitId benefitId : optionalCoverageBenefits.get(coverageId)) {
                MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                        .addValue("planId", event.getPlanId().toString())
                        .addValue("coverageId", coverageId.toString())
                        .addValue("benefitId", benefitId.toString())
                        .addValue("planName", event.getPlanName())
                        .addValue("planCode", event.getPlanCode())
                        .addValue("launchDate", new Date(event.getLaunchDate().toDate().getTime()))
                        .addValue("lineOfBusiness", event.getLineOfBusinessId().toString())
                        .addValue("withdrawalDate", event.getWithdrawalDate() != null ? new Date(event.getWithdrawalDate().toDate().getTime()) : null)
                        .addValue("funeralCover", event.isFuneralCover())
                        .addValue("clientType", event.getClientType().toString());
                namedParameterJdbcTemplate.execute("insert into plan_coverage_benefits_assoc (`plan_id`,`plan_name`,`plan_code`,`launch_date`,`withdrawal_date`,`line_of_business`,`client_type`," +
                                "`coverage_id`,`benefit_id`,`funeral_cover`,`optional`) values (:planId,:planName,:planCode,:launchDate,:withdrawalDate,:lineOfBusiness,:clientType," +
                                ":coverageId,:benefitId,0,:funeralCover)", parameterSource,
                        new PreparedStatementCallback<Object>() {
                            @Override
                            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                                return ps.execute();
                            }
                        });
            }
        }
    }

    private void populateBaseCoverages(PlanCoverageAssociationEvent event, Map<CoverageId, List<BenefitId>> baseCoverageBenefits) {
        if (baseCoverageBenefits == null) return;

        for (CoverageId coverageId : baseCoverageBenefits.keySet()) {
            for (BenefitId benefitId : baseCoverageBenefits.get(coverageId)) {
                MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                        .addValue("planId", event.getPlanId().toString())
                        .addValue("coverageId", coverageId.toString())
                        .addValue("benefitId", benefitId.toString())
                        .addValue("planName", event.getPlanName())
                        .addValue("planCode", event.getPlanCode())
                        .addValue("launchDate", new Date(event.getLaunchDate().toDate().getTime()))
                        .addValue("lineOfBusiness", event.getLineOfBusinessId().toString())
                        .addValue("withdrawalDate", event.getWithdrawalDate() != null ? new Date(event.getWithdrawalDate().toDate().getTime()) : null)
                        .addValue("funeralCover", event.isFuneralCover())
                        .addValue("clientType", event.getClientType().toString());
                namedParameterJdbcTemplate.execute("insert into plan_coverage_benefits_assoc (`plan_id`,`plan_name`,`plan_code`,`launch_date`,`withdrawal_date`,`line_of_business`,`client_type`,`coverage_id`," +
                                "`benefit_id`,`optional`,`funeral_cover`) values (:planId,:planName,:planCode,:launchDate,:withdrawalDate,:lineOfBusiness,:clientType,:coverageId,:benefitId,1,:funeralCover)", parameterSource,
                        new PreparedStatementCallback<Object>() {
                            @Override
                            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                                return ps.execute();
                            }
                        });
            }
        }
    }

    public void handle(PlanLaunchEvent event) {
        String planId = event.getPlanId().toString();
        namedParameterJdbcTemplate.execute("update from plan_coverage_benefits_assoc set plan_status='" + PlanStatus.LAUNCHED + "' where " +
                        "plan_id='" + planId + "'",
                new EmptySqlParameterSource(), new PreparedStatementCallback<Object>() {
                    @Override
                    public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                        return ps.execute();
                    }
                });
    }
}
