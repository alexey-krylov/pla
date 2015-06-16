package com.pla.core.presentation.command;

import com.pla.core.domain.exception.DuplicatePlanException;
import com.pla.core.domain.exception.PlanException;
import com.pla.core.domain.exception.PlanValidationException;
import com.pla.core.domain.model.plan.*;
import com.pla.core.specification.PlanCodeSpecification;
import com.pla.sharedkernel.domain.model.CoverageTermType;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */
@Component
public class PlanCommandHandler {

    private Repository<Plan> planMongoRepository;
    private PlanCodeSpecification planCodeSpecification;
    private SequenceGenerator sequenceGenerator;

    private String DUPLICATE_PLAN_CODE = "Plan with same name %s already exists.";

    @Autowired
    public PlanCommandHandler(Repository<Plan> planMongoRepository, PlanCodeSpecification planCodeSpecification, SequenceGenerator sequenceGenerator) {
        this.planMongoRepository = planMongoRepository;
        this.planCodeSpecification = planCodeSpecification;
        this.sequenceGenerator = sequenceGenerator;
    }

    @CommandHandler
    public void handle(CreatePlanCommand command) throws DuplicatePlanException, PlanValidationException, PlanException {
        try {
            boolean isSatisfied = planCodeSpecification.satisfiedOnCreate(command.getPlanId(), command.getPlanDetail().getPlanName(),
                    command.getPlanDetail().getPlanCode());
            if (!isSatisfied) {
                throw new DuplicatePlanException(String.format(DUPLICATE_PLAN_CODE, command.getPlanDetail().getPlanName()));
            }
            String planCode = sequenceGenerator.getSequence(Plan.class);
            command.getPlanDetail().setPlanCode(planCode);
            PlanBuilder planBuilder = planBuilder(command);
            Plan plan = planBuilder.build(command.getPlanId());
            planMongoRepository.add(plan);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PlanException(e.getMessage());
        }
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

                .withLineOfBusinessId(dtl.getLineOfBusinessId())
                .withMaxEntryAge(dtl.getMaxEntryAge())
                .withMinEntryAge(dtl.getMinEntryAge())
                .withPlanCode(dtl.getPlanCode())
                .withPlanType(dtl.getPlanType())
                .withSurrenderAfter(dtl.getSurrenderAfter())
                .withFuneralCover(dtl.funeralCover);
        if (dtl.getWithdrawalDate() != null) {
            pdBuilder.withWithdrawalDate(dtl.getWithdrawalDate());
        }
        PlanDetail pd = pdBuilder.build(false);

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
            pcBuilder.withTaxApplicable(each.isTaxApplicable());
            pcBuilder.withCoverageType(each.getCoverageType());
            pcBuilder.withDeductibleType(each.getDeductibleType());
            pcBuilder.withDeductible(each.getDeductibleAmount());
            pcBuilder.withMinAndMaxAge(each.getMinAge(), each.getMaxAge());
            pcBuilder.withWaitingPeriod(each.getWaitingPeriod());

            for (MaturityAmountDetail maturityRec : each.getMaturityAmounts())
                pcBuilder.withMaturityAmount(maturityRec.getMaturityYear(), maturityRec.getGuaranteedSurvivalBenefitAmount());


            pcBuilder.withSumAssuredForPlanCoverage(each.getCoverageSumAssured().getSumAssuredType(), each.getCoverageSumAssured().getMinSumInsured(),
                    SumAssuredType.DERIVED.equals(each.getCoverageSumAssured().getSumAssuredType()) ? each.getCoverageSumAssured().getMaxLimit() : each.getCoverageSumAssured().getMaxSumInsured(), each.getCoverageSumAssured().getMultiplesOf(), each.getCoverageSumAssured().getSumAssuredValue(),
                    each.getCoverageSumAssured().getPercentage());

            for (Iterator<PlanCoverageBenefitDetail> iter = filterBenefits(command.getPlanCoverageBenefits(),
                    each.getCoverageId());
                 iter.hasNext(); ) {
                PlanCoverageBenefitDetail rec = iter.next();
                pcBuilder.withBenefitLimit(each.getCoverageId(), rec.getCoverageName(), rec.getBenefitName(), rec.getBenefitId().toString(),
                        rec.getDefinedPer(), rec.getCoverageBenefitType(), rec.getBenefitLimit(), rec.getMaxLimit());
            }
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
                command.getSumAssured().getIncomeMultiplier());
        planBuilder.withPolicyTerm(command.getPolicyTermType(), command.getPolicyTerm().getValidTerms(), command.getPolicyTerm().getMaxMaturityAge(), command.getPolicyTerm().getGroupTerm());
        System.out.println(command.getPremiumTerm());
        planBuilder.withPremiumTerm(command.getPremiumTermType(), command.getPremiumTerm().getValidTerms(), command.getPremiumTerm().getMaxMaturityAge());
        planBuilder.withPlanCoverages(coverageSet);
        return planBuilder;
    }

    private Iterator<PlanCoverageBenefitDetail> filterBenefits(List<PlanCoverageBenefitDetail> benefits, CoverageId coverageId) {
        return benefits.stream().filter(new Predicate<PlanCoverageBenefitDetail>() {
            @Override
            public boolean test(PlanCoverageBenefitDetail planCoverageBenefitDetail) {
                return planCoverageBenefitDetail.getCoverageId().equals(coverageId);
            }
        }).iterator();
    }

    @CommandHandler
    public void handle(UpdatePlanCommand command) throws DuplicatePlanException, PlanValidationException, PlanException {
        try {
            boolean isSatisfied = planCodeSpecification.satisfiedOnUpdate(command.getPlanId(), command.getPlanDetail().getPlanName(),
                    command.getPlanDetail().getPlanCode());
            if (!isSatisfied) {
                throw new DuplicatePlanException(String.format(DUPLICATE_PLAN_CODE, command.getPlanDetail().getPlanName()));
            }
            PlanBuilder planBuilder = planBuilder(command);
            Plan plan = planMongoRepository.load(command.getPlanId());
            plan.updatePlan(planBuilder);
        } catch (Exception e) {
            throw new PlanException(e.getMessage());
        }
    }

    @CommandHandler
    public void handle(ExpirePlanCommand command) throws DuplicatePlanException, PlanValidationException {
        Plan plan = planMongoRepository.load(command.getPlanId());
        plan.withdrawPlan();
    }
}
