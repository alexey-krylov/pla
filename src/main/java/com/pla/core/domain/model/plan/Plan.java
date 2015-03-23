package com.pla.core.domain.model.plan;

import com.google.common.base.Preconditions;
import com.pla.sharedkernel.domain.event.*;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcedMember;
import org.nthdimenzion.utils.UtilValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@ToString(exclude = {"logger"})
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode(exclude = {"logger", "specification"}, callSuper = false)
public class Plan extends AbstractAnnotatedAggregateRoot<PlanId> {

    private Logger logger = LoggerFactory.getLogger(Plan.class);

    @AggregateIdentifier
    private PlanId planId;
    @EventSourcedMember
    private PlanDetail planDetail;
    private PlanSpecification specification = new PlanSpecification();

    @SuppressWarnings("UnusedDeclaration")
    protected Plan() {
    }

    Plan(PlanId planId, PlanDetail pd) {

        Preconditions.checkArgument(planId != null);
        this.planId = planId;
        apply(new PlanConfigured(planId));

        Preconditions.checkArgument(pd != null);
        apply(new PlanDetailConfigured(this.planId, pd.planName,
                pd.planCode,
                pd.launchDate,
                pd.withdrawalDate,
                pd.freeLookPeriod,
                pd.minEntryAge,
                pd.maxEntryAge,
                pd.taxApplicable,
                pd.surrenderAfter,
                pd.applicableRelationships,
                pd.endorsementTypes,
                pd.lineOfBusinessId,
                pd.planType,
                pd.clientType));

    }

    public void updatePlanDetail(PlanDetail pd) {
        Preconditions.checkArgument(pd != null);
        apply(new PlanDetailChanged(this.planId, pd.planName,
                pd.planCode,
                pd.launchDate,
                pd.withdrawalDate,
                pd.freeLookPeriod,
                pd.minEntryAge,
                pd.maxEntryAge,
                pd.taxApplicable,
                pd.surrenderAfter,
                pd.applicableRelationships,
                pd.endorsementTypes,
                pd.lineOfBusinessId,
                pd.planType,
                pd.clientType));
    }


    public void configureMaturityAmount(Set<MaturityAmount> maturityAmounts) {
        apply(new MaturityConfigured(maturityAmounts));
    }

    public void configureCoverageBenefits(Set<PlanCoverageBenefit> benefits) {
        apply(new CoverageBenefitConfigured(benefits));
    }

    public void configurePolicyTerm(PolicyTermType policyTermType, Set<Integer> validValues, int maxMaturityAge) {
        Preconditions.checkArgument(policyTermType != null);
        Preconditions.checkArgument(validValues != null);
        apply(new PolicyTermConfigured(this.planId, policyTermType, validValues, maxMaturityAge));
        checkState(specification.isSatisfiedBy(this), " Premium Payment Term is greater than the Policy Term.");
    }

    /**
     * /**
     * If ValidValues is empty or null, the PremiumTermType should
     * be PremiumTermType.REGULAR.
     *
     * @param paymentTermType
     * @param validValues
     * @param cutOffAge
     */
    public void configurePremiumPayment(PremiumTermType paymentTermType,
                                        Set<Integer> validValues, int cutOffAge) {
        if (PremiumTermType.REGULAR == paymentTermType) {
            Preconditions.checkArgument(validValues == null);
        }
        apply(new PremiumTermConfigured(this.planId, paymentTermType, validValues, cutOffAge));
        checkState(specification.isSatisfiedBy(this), " Premium Payment Term is greater than the Policy Term.");
    }

    public void configureSumAssured(SumAssuredType sumAssuredType,
                                    BigDecimal minSumAssuredAmount,
                                    BigDecimal maxSumAssuredAmount,
                                    int multiplesOf,
                                    Set<BigDecimal> assuredValues,
                                    int percentage) {
        checkState(planDetail != null, "Plan is not in a valid state.");
        SumAssuredConfigured event = null;
        switch (sumAssuredType) {
            case SPECIFIED_VALUES:
                Preconditions.checkArgument(UtilValidator.isNotEmpty(assuredValues));
                event = new SumAssuredConfigured(this.planId, assuredValues);
                break;
            case DERIVED:
                Preconditions.checkArgument(SumAssuredType.DERIVED != sumAssuredType);
            case RANGE:
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1,
                        "MinSumAssuredAmount greater than zero Expected, but got %d", minSumAssuredAmount);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1,
                        "MaxSumAssuredAmount greater than zero Expected, but got %d", maxSumAssuredAmount);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(minSumAssuredAmount) == 1,
                        "MaxSumAssuredAmount>MinSumAssuredAmount Expected, but %d>%d",
                        maxSumAssuredAmount, minSumAssuredAmount);
                Preconditions.checkArgument(multiplesOf % 10 == 0, " Not valid Multiples.");
                event = new SumAssuredConfigured(this.planId,
                        minSumAssuredAmount, maxSumAssuredAmount, multiplesOf);
                break;
        }
        checkState(event != null, " Sum Assured Type supplied are not valid.");
        apply(event);
    }

    public void configureCoverages(Set<PlanCoverage> coverages) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(coverages));
        apply(new PlanCoverageReconfigured(coverages));
    }

    @EventHandler
    protected void onPlanConfigured(PlanConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan was created with Plan Id " + event.getPlanId());
        }
        this.planId = event.getPlanId();
    }

    @EventHandler
    protected void onPlanDetailConfigured(PlanDetailConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Detail configured");
        }
        this.planDetail = PlanDetail.builder().withPlanName(event.getPlanName())
                .withPlanCode(event.getPlanCode())
                .withLaunchDate(event.getLaunchDate())
                .withWithdrawalDate(event.getWithdrawalDate())
                .withMinEntryAge(event.getMinEntryAge())
                .withMaxEntryAge(event.getMaxEntryAge())
                .withFreeLookPeriod(event.getFreeLookPeriod())
                .withSurrenderAfter(event.getSurrenderAfter())
                .withClientType(event.getClientType())
                .withLineOfBusinessId(event.getLineOfBusinessId())
                .withPlanType(event.getPlanType())
                .withApplicableRelationships(event.getApplicableRelationships())
                .withEndorsementTypes(event.getEndorsementTypes())
                .withTaxApplicable(event.isTaxApplicable())
                .build();
    }

    @EventHandler
    protected void onPlanDetailChanged(PlanDetailChanged event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Detail changed.");
        }
        this.planDetail = PlanDetail.builder().withPlanName(event.getPlanName())
                .withPlanCode(event.getPlanCode())
                .withLaunchDate(event.getLaunchDate())
                .withWithdrawalDate(event.getWithdrawalDate())
                .withMinEntryAge(event.getMinEntryAge())
                .withMaxEntryAge(event.getMaxEntryAge())
                .withFreeLookPeriod(event.getFreeLookPeriod())
                .withSurrenderAfter(event.getSurrenderAfter())
                .withClientType(event.getClientType())
                .withLineOfBusinessId(event.getLineOfBusinessId())
                .withPlanType(event.getPlanType())
                .withApplicableRelationships(event.getApplicableRelationships())
                .withEndorsementTypes(event.getEndorsementTypes())
                .withTaxApplicable(event.isTaxApplicable())
                .build();
    }

    public PlanId getIdentifier() {
        return planId;
    }

    public void configureSumAssuredForPlanCoverage(CoverageId coverageId,
                                                   SumAssuredType sumAssuredType,
                                                   BigDecimal minSumAssuredAmount,
                                                   BigDecimal maxSumAssuredAmount,
                                                   int multiplesOf,
                                                   Set<BigDecimal> assuredValues,
                                                   int percentage) {
        checkState(this.planDetail != null);
        PlanCoverageSumAssuredConfigured event = null;
        switch (sumAssuredType) {
            case SPECIFIED_VALUES:
                Preconditions.checkArgument(UtilValidator.isNotEmpty(assuredValues));
                event = new PlanCoverageSumAssuredConfigured(this.planId, coverageId, assuredValues);
                break;
            case DERIVED:
                Preconditions.checkArgument(percentage > 0);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1);
                Preconditions.checkArgument(coverageId != null);
                event = new PlanCoverageSumAssuredConfigured(this.planId, coverageId, percentage, maxSumAssuredAmount);
                break;
            case RANGE:
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1,
                        "MinSumAssuredAmount greater than zero Expected, but got %d", minSumAssuredAmount);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(BigDecimal.ZERO) == 1,
                        "MaxSumAssuredAmount greater than zero Expected, but got %d", maxSumAssuredAmount);
                Preconditions.checkArgument(maxSumAssuredAmount.compareTo(minSumAssuredAmount) == 1,
                        "MaxSumAssuredAmount>MinSumAssuredAmount Expected, but %d>%d",
                        maxSumAssuredAmount, minSumAssuredAmount);
                Preconditions.checkArgument(multiplesOf % 10 == 0, " Not valid Multiples.");
                event = new PlanCoverageSumAssuredConfigured(this.planId, coverageId,
                        minSumAssuredAmount, maxSumAssuredAmount, multiplesOf);
                break;
        }
        checkState(event != null, " Sum Assured Type supplied are not valid.");
        apply(event);
    }

    public void configureTermForPlanCoverage(CoverageId coverageId, CoverageTermType coverageTermType, Set<Integer> validTerms, int maxMaturityAge) {
        checkState(this.planDetail != null);
        if (CoverageTermType.SPECIFIED_VALUES == coverageTermType)
            checkState(specification.checkCoverageTerm(this, validTerms), " Coverage Term is greater than the Policy Term.");
        apply(new PlanCoverageTermConfigured(this.planId, coverageId, coverageTermType, validTerms, maxMaturityAge));
    }
}