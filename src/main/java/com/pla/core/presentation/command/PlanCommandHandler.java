package com.pla.core.presentation.command;

import com.pla.core.domain.model.plan.*;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */
@Component
public class PlanCommandHandler {

    @Autowired
    private MongoTemplate mongoTemplate;

    @CommandHandler
    public void handle(CreatePlanCommand command) {
        Plan plan = buildPlan(command);
        mongoTemplate.save(plan);
    }

    private Plan buildPlan(CreatePlanCommand command) {
        System.out.println(command);
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
        System.out.println(pd);

        Set<PlanCoverage> coverageSet = new HashSet<PlanCoverage>();
        for (PlanCoverageDetail each : command.getCoverages()) {
            PlanCoverageBuilder pcBuilder = PlanCoverage.builder();
            pcBuilder.withCoverageCover(each.getCoverageCover());
            pcBuilder.withCoverage(each.getCoverageId());
            pcBuilder.withCoverageTerm(each.getCoverageTermType(), each.getCoverageTerm().getValidTerms(), each.getCoverageTerm().getMaxMaturityAge());
            pcBuilder.withTaxApplicable(each.getTaxApplicable());
            pcBuilder.withCoverageType(each.getCoverageType());
            pcBuilder.withDeductibleType(each.getDeductibleType());
            pcBuilder.withDeductible(each.getDeductibleAmount());
            for (MaturityAmountDetail maturityRec : each.getMaturityAmounts())
                pcBuilder.withMaturityAmount(maturityRec.getMaturityYear(), maturityRec.getGuaranteedSurvivalBenefitAmount());
            pcBuilder.withMinAndMaxAge(each.getMinAge(), each.getMaxAge());
            pcBuilder.withWaitingPeriod(each.getWaitingPeriod());
            pcBuilder.withSumAssuredForPlanCoverage(each.getSumAssured().getSumAssuredType(), each.getSumAssured().getMinSumInsured(),
                    each.getSumAssured().getMaxSumInsured(), each.getSumAssured().getMultiplesOf(), each.getSumAssured().getSumAssuredValue(),
                    each.getSumAssured().getPercentage());
            for (PlanCoverageBenefitDetail rec : each.getPlanCoverageBenefits())
                pcBuilder.withBenefitLimit(rec.getBenefitId().toString(), rec.getDefinedPer(), rec.getCoverageBenefitType(), rec.getBenefitLimit(), rec.getMaxLimit());
            PlanCoverage planCoverage = pcBuilder.build();
            System.out.println(planCoverage);
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
        Plan plan = planBuilder.build(command.getPlanId());
        return plan;
    }

    @CommandHandler
    public void handle(UpdatePlanCommand command) {
        Plan plan = buildPlan(command);
        mongoTemplate.save(plan);
    }
}
