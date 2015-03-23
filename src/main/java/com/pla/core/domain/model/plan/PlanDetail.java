package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.domain.event.*;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedEntity;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PlanDetail extends AbstractAnnotatedEntity {

    String planName;
    String planCode;
    LocalDate launchDate;
    LocalDate withdrawalDate;
    int freeLookPeriod = 15;
    int minEntryAge;
    int maxEntryAge;
    boolean taxApplicable;
    int surrenderAfter;
    Set<Relationship> applicableRelationships;
    Set<EndorsementType> endorsementTypes;
    LineOfBusinessId lineOfBusinessId;
    PlanType planType;
    ClientType clientType;
    private Logger logger = LoggerFactory.getLogger(Plan.class);
    private SumAssured sumAssured;
    private PolicyTermType policyTermType;
    private PremiumTermType premiumTermType;
    private Term premiumTerm;
    private Collection<MaturityAmount> maturityAmounts;

    /**
     * Policy term can be a list of age with upper band
     * of maximum maturity age OR it could be list of age
     * of the insured.
     */
    private Term policyTerm;
    private Set<PlanCoverage> coverages = new HashSet<PlanCoverage>();
    private CoverageTermType coverageTermType;

    PlanDetail(final PlanDetailBuilder planDetailBuilder) {
        checkArgument(UtilValidator.isNotEmpty(planDetailBuilder.planName), "Plan Name cannot be empty.");
        this.planName = planDetailBuilder.planName;

        checkArgument(planDetailBuilder.planCode != null);
        this.planCode = planDetailBuilder.planCode;

        checkArgument(planDetailBuilder.minEntryAge > 0, "Min Entry Age cannot be less than 0");
        this.minEntryAge = planDetailBuilder.minEntryAge;

        checkArgument(planDetailBuilder.maxEntryAge > planDetailBuilder.minEntryAge, "Max Entry Age cannot be less than Min Entry Age");
        this.maxEntryAge = planDetailBuilder.maxEntryAge;

        checkArgument(planDetailBuilder.launchDate != null, "Cannot create a Plan with Launch Date");
        checkArgument(planDetailBuilder.launchDate.isAfter(LocalDate.now()), "Cannot create a Plan with Launch Date less than Today's date");

        this.launchDate = planDetailBuilder.launchDate;

        checkArgument(planDetailBuilder.withdrawalDate.isAfter(launchDate), "Withdrawal cannot be less than launchDate");
        this.withdrawalDate = planDetailBuilder.withdrawalDate;


        checkArgument(planDetailBuilder.clientType != null, "Cannot create Plan without Client Type");
        this.clientType = planDetailBuilder.clientType;

        checkArgument(UtilValidator.isNotEmpty(planDetailBuilder.endorsementTypes));
        this.endorsementTypes = planDetailBuilder.endorsementTypes;

        if (this.clientType.equals(ClientType.INDIVIDUAL)) {
            this.surrenderAfter = planDetailBuilder.surrenderAfterYears;
            Stream<EndorsementType> groupEndorsementType = this.endorsementTypes.stream().filter(endorsementType ->
                            endorsementType.equals(EndorsementType.MEMBER_ADDITION)
                                    || endorsementType.equals(EndorsementType.MEMBER_DELETION)
                                    || endorsementType.equals(EndorsementType.PROMOTION)
                                    || endorsementType.equals(EndorsementType.NEW_COVER)
            );
            checkArgument(groupEndorsementType.count() == 0, "Group Endorsements are not allowed for Plan with Client Type as %s", this.clientType);
        }

        checkArgument(planDetailBuilder.lineOfBusinessId != null, "Cannot create Plan without Line of Business");
        this.lineOfBusinessId = planDetailBuilder.lineOfBusinessId;

        checkArgument(planDetailBuilder.planType != null, "Cannot create Plan without Plan Type");
        this.planType = planDetailBuilder.planType;

        this.freeLookPeriod = planDetailBuilder.freeLookPeriod;
        this.taxApplicable = planDetailBuilder.taxApplicable;
        this.applicableRelationships = planDetailBuilder.applicableRelationships;

    }

    public static PlanDetailBuilder builder() {
        return new PlanDetailBuilder();
    }

    @EventHandler
    protected void onPolicyTermConfigured(PolicyTermConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Policy Term configured");
        }
        this.policyTermType = event.getPolicyTermType();
        if (PolicyTermType.MATURITY_AGE_DEPENDENT == event.getPolicyTermType())
            this.policyTerm = new Term(event.getValidTerms());
        else if (PolicyTermType.SPECIFIED_VALUES == event.getPolicyTermType()) {
            this.policyTerm = new Term(event.getValidTerms(), event.getMaxMaturityAge());
        }
    }

    @EventHandler
    protected void onPremiumTermConfigured(PremiumTermConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Premium Term configured");
        }
        this.premiumTermType = event.getTermType();
        switch (event.getTermType()) {
            case SPECIFIED_VALUES:
                this.premiumTerm = new Term(event.getValidTerms(), event.getPremiumCutOffAge());
                break;
            case SPECIFIED_AGES:
                this.premiumTerm = new Term(event.getValidTerms());
                break;
            case REGULAR:
                this.premiumTerm = policyTerm;
                break;
            case SINGLE:
                //TODO find out what happens when it is single premium
        }
    }

    @EventHandler
    protected void onSumAssuredConfigured(SumAssuredConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Sum Assured configured");
        }
        SumAssured sumAssured = null;
        switch (event.getSumAssuredType()) {
            case RANGE:
                sumAssured = new SumAssured(event.getMinSumAssuredAmount(),
                        event.getMaxSumAssuredAmount(), event.getMultiplesOf());
                break;
            case SPECIFIED_VALUES:
                sumAssured = new SumAssured(event.getAssuredValues());
                break;
        }
        checkArgument(sumAssured != null);
        this.sumAssured = sumAssured;
    }

    @EventHandler
    protected void onPlanCoverageConfigured(PlanCoverageConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Coverage configured");
        }
        this.coverages = event.getCoverages();
    }

    @EventHandler
    protected void onPlanCoverageSumAssuredConfigured(PlanCoverageSumAssuredConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Coverage Sum Assured re-configured");
        }
        checkArgument(event.getCoverageId() != null);
        CoverageId coverageId = event.getCoverageId();
        Optional<PlanCoverage> result = coverages.stream().filter(pc -> pc.getCoverageId().equals(coverageId)).findFirst();
        checkArgument(result.isPresent(), " Cannot find Plan Coverage with the coverage id supplied. %s", coverageId);

        PlanCoverage pc = result.get();
        SumAssured sumAssured = null;
        switch (event.getSumAssuredType()) {
            case RANGE:
                sumAssured = new SumAssured(event.getMinSumAssuredAmount(),
                        event.getMaxSumAssuredAmount(), event.getMultiplesOf());
                break;
            case SPECIFIED_VALUES:
                sumAssured = new SumAssured(event.getAssuredValues());
                break;
            case DERIVED:
                sumAssured = new SumAssured(event.getCoverageId(), event.getPercentage(), BigInteger.valueOf(event.getMaxSumAssuredAmount().longValue()));
                break;
        }
        checkArgument(sumAssured != null);
        pc.configureSumAssured(sumAssured);
    }

    @EventHandler
    protected void onPlanCoverageTermConfigured(PlanCoverageTermConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Coverage Term configured.");
        }
        CoverageId coverageId = event.getCoverageId();
        Optional<PlanCoverage> result = coverages.stream().filter(pc -> pc.getCoverageId().equals(coverageId)).findFirst();
        checkArgument(result.isPresent(), " Cannot find Plan Coverage with the coverage id supplied. %s", coverageId);

        this.coverageTermType = event.getCoverageTermType();
        PlanCoverage pc = result.get();
        switch (event.getCoverageTermType()) {
            case SPECIFIED_VALUES:
                checkArgument(event.getMaxMaturityAge() > 0);
                checkArgument(UtilValidator.isNotEmpty(event.getValidTerms()));
                pc.configureCoverageTerm(new Term(event.getValidTerms(), event.getMaxMaturityAge()));
                break;
            case AGE_DEPENDENT:
                checkArgument(UtilValidator.isNotEmpty(event.getValidTerms()));
                pc.configureCoverageTerm(new Term(event.getValidTerms()));
                break;
            case POLICY_TERM:
                checkState(policyTerm != null, "Policy Term is not configured.");
                pc.configureCoverageTerm(new Term(policyTerm));
                apply(new PlanCoverageRegularTermConfigured(event.getPlanId(), event.getCoverageId(),
                        CoverageTermType.POLICY_TERM, policyTerm.getValidTerms(), policyTerm.getMaxMaturityAge()));
                break;
        }
    }

    @EventHandler
    protected void onMaturityAmountConfigured(MaturityConfigured event) {
        checkArgument(event.getMaturityAmounts() != null);
        this.maturityAmounts = event.getMaturityAmounts();
    }


    @EventHandler
    protected void onCoverageBenefitConfigured(CoverageBenefitConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Coverage Sum Assured re-configured");
        }
        PlanCoverage previousPC = null;
        Set<PlanCoverageBenefit> benefitForCoverage = new HashSet<>();
        for (PlanCoverageBenefit each : event.getBenefits()) {
            CoverageId coverageId = each.getCoverageId();
            Optional<PlanCoverage> result = coverages.stream().filter(pc -> pc.getCoverageId().equals(coverageId)).findFirst();
            checkArgument(result.isPresent(), " Cannot find Plan Coverage with the coverage id supplied. %s", coverageId);
            PlanCoverage pc = result.get();
            if (previousPC != null && !pc.equals(previousPC)) {
                previousPC.replacePlanCoverageBenefits(new HashSet<>(benefitForCoverage));
                benefitForCoverage.clear();
            }
            benefitForCoverage.add(each);
            previousPC = pc;
        }
        if (benefitForCoverage.size() > 0) {
            previousPC.replacePlanCoverageBenefits(new HashSet(benefitForCoverage));
        }
        benefitForCoverage.clear();
    }

}
