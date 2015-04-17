package com.pla.core.presentation.command;

import com.pla.core.domain.model.plan.*;
import com.pla.sharedkernel.domain.model.CoverageTermType;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */
@Component
public class PlanCommandHandler {

    private Repository<Plan> planMongoRepository;

    @Autowired
    public PlanCommandHandler(Repository<Plan> planMongoRepository) {
        this.planMongoRepository = planMongoRepository;
    }

    @CommandHandler
    public void handle(CreatePlanCommand command) {
        PlanBuilder planBuilder = planBuilder(command);
        Plan plan = planBuilder.build(command.getPlanId());
        planMongoRepository.add(plan);
    }

    private PlanBuilder planBuilder(CreatePlanCommand command) {
        PlanDetailBuilder pdBuilder = PlanDetail.builder();
        Detail dtl = command.getPlanDetail();
        pdBuilder.withPlanName(dtl.getPlanName())
                .withTaxApplicable(dtl.isTaxApplicable())
                .withApplicableRelationships(dtl.getApplicableRelationships())
                .withClientType(dtl.getClientType())
                .withEndorsementTypes(dtl.getEndorsementTypes())
                .withFreeLookPeriod(dtl.getFreeLookPeriod())
                .withLaunchDate(dtl.getLaunchDate())
                .withWithdrawalDate(dtl.getWithdrawalDate())
                .withLineOfBusinessId(dtl.getLineOfBusinessId())
                .withMaxEntryAge(dtl.getMaxEntryAge())
                .withMinEntryAge(dtl.getMinEntryAge())
                .withPlanCode(dtl.getPlanCode())
                .withPlanType(dtl.getPlanType())
                .withSurrenderAfter(dtl.getSurrenderAfter());
        PlanDetail pd = pdBuilder.build();

        Set<PlanCoverage> coverageSet = new HashSet<PlanCoverage>();
        for (PlanCoverageDetail each : command.getCoverages()) {
            PlanCoverageBuilder pcBuilder = PlanCoverage.builder();
            pcBuilder.withCoverageCover(each.getCoverageCover());
            pcBuilder.withCoverage(each.getCoverageId());
            Term coverageTerm = null;
            if (CoverageTermType.AGE_DEPENDENT == each.getCoverageTermType()) {
                coverageTerm = new Term(each.getCoverageTerm().getMaturityAges());
            } else if (CoverageTermType.SPECIFIED_VALUES == each.getCoverageTermType()) {
                coverageTerm = new Term(each.getCoverageTerm().getValidTerms(), each.getCoverageTerm().getMaxMaturityAge());
            }
            pcBuilder.withCoverageTerm(each.getCoverageTermType(), coverageTerm);
            pcBuilder.withTaxApplicable(each.getTaxApplicable());
            pcBuilder.withCoverageType(each.getCoverageType());
            pcBuilder.withDeductibleType(each.getDeductibleType());
            pcBuilder.withDeductible(each.getDeductibleAmount());
            for (MaturityAmountDetail maturityRec : each.getMaturityAmounts())
                pcBuilder.withMaturityAmount(maturityRec.getMaturityYear(), maturityRec.getGuaranteedSurvivalBenefitAmount());
            pcBuilder.withMinAndMaxAge(each.getMinAge(), each.getMaxAge());
            pcBuilder.withWaitingPeriod(each.getWaitingPeriod());

            pcBuilder.withSumAssuredForPlanCoverage(each.getCoverageSumAssured().getSumAssuredType(), each.getCoverageSumAssured().getMinSumInsured(),
                    each.getCoverageSumAssured().getMaxSumInsured(), each.getCoverageSumAssured().getMultiplesOf(), each.getCoverageSumAssured().getSumAssuredValue(),
                    each.getCoverageSumAssured().getPercentage());

            for (PlanCoverageBenefitDetail rec : each.getPlanCoverageBenefits())
                pcBuilder.withBenefitLimit(rec.getBenefitId().toString(), rec.getDefinedPer(), rec.getCoverageBenefitType(), rec.getBenefitLimit(), rec.getMaxLimit());

            PlanCoverage planCoverage = pcBuilder.build();
            coverageSet.add(planCoverage);
        }

        PlanBuilder planBuilder = Plan.builder();
        planBuilder.withPlanDetail(pd);
        planBuilder.withPlanSumAssured(command.getSumAssured().getSumAssuredType(),
                command.getSumAssured().getMinSumInsured(),
                command.getSumAssured().getMaxSumInsured(),
                command.getSumAssured().getMultiplesOf(),
                command.getSumAssured().getSumAssuredValue(),
                command.getSumAssured().getPercentage());
        planBuilder.withPolicyTerm(command.getPolicyTermType(), command.getPolicyTerm().getValidTerms(), command.getPolicyTerm().getMaxMaturityAge());
        planBuilder.withPremiumTerm(command.getPremiumTermType(), command.getPremiumTerm().getValidTerms(), command.getPremiumTerm().getMaxMaturityAge());
        planBuilder.withPlanCoverages(coverageSet);
        return planBuilder;
    }

    @CommandHandler
    public void handle(UpdatePlanCommand command) {
        Plan oldPlan = planMongoRepository.load(command.getPlanId());
        oldPlan.delete();
        PlanBuilder planBuilder = planBuilder(command);
        Plan _new = planBuilder.build(command.getNewPlanId());
        planMongoRepository.add(_new);
    }
}
