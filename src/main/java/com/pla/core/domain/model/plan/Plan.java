package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.domain.AbstractAggregateRoot;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@ToString
@Getter(AccessLevel.PACKAGE)
public class Plan extends AbstractAggregateRoot<PlanId> {

    private PlanId planId;
    private PlanDetail planDetail;
    private SumAssured sumAssured;
    /**
     * Policy term can be a list of age with upper band
     * of maximum maturity age OR it could be list of age
     * of the insured.
     */
    private PolicyTerm policyTerm;
    private PlanPayment planPayment;
    private Set<PlanCoverage> coverages;

    protected Plan() {
    }

    private Plan(PlanBuilder builder) {
        checkArgument(builder.planId != null);
        this.planId = builder.planId;

        checkArgument(UtilValidator.isNotEmpty(builder.coverages));
        this.coverages = builder.coverages;

        checkArgument(builder.planPayment != null);
        this.planPayment = builder.planPayment;

        builder.maturityAmountSet.forEach(amt -> this.planPayment.addMaturityAmount(amt));

        checkArgument(builder.policyTerm != null);
        this.policyTerm = builder.policyTerm;

        checkArgument(builder.planDetail != null);
        this.planDetail = builder.planDetail;

        checkArgument(builder.sumAssured != null);
        this.sumAssured = builder.sumAssured;

    }

    public static PlanBuilder builder() {
        return new PlanBuilder();
    }

    public PlanId getIdentifier() {
        return planId;
    }

    public static class PlanBuilder {

        private PlanId planId;
        private PlanDetail planDetail;
        private SumAssured sumAssured;
        private PolicyTerm policyTerm;
        private PlanPayment planPayment;
        private Set<PlanCoverage> coverages;
        private Set<MaturityAmount> maturityAmountSet = new HashSet<MaturityAmount>();

        public PlanBuilder withPlanId(PlanId planId) {
            this.planId = planId;
            return this;
        }


        public PlanBuilder withPlanDetail(PlanDetail planDetail) {
            this.planDetail = planDetail;
            return this;
        }


        public PlanBuilder withSumAssuredByRange(BigDecimal minSumInsured, BigDecimal maxSumInsured, int multiplesOf) {
            this.sumAssured = new SumAssuredByRange(minSumInsured, maxSumInsured, multiplesOf);
            return this;
        }

        public PlanBuilder withSumAssuredBasedOnValue(Set<BigDecimal> sumInsuredValues) {
            this.sumAssured = new SumAssured(sumInsuredValues);
            return this;
        }


        public PlanBuilder withPolicyTermBasedOnAge(int maxMaturityAge) {
            this.policyTerm = new PolicyTerm(maxMaturityAge);
            return this;
        }

        public PlanBuilder withPolicyTermBasedOnValue(Set<Integer> validTerms, int maxMaturityAge) {
            this.policyTerm = new PolicyTerm(validTerms, maxMaturityAge);
            return this;
        }

        public PlanBuilder withPaymentTermBasedOnAge(int paymentCutOffAge) {
            this.planPayment = new PlanPayment(new PremiumPayment(paymentCutOffAge));
            return this;
        }

        public PlanBuilder withPaymentTermBasedOnValue(Set<Integer> validTerms, int maxMaturityAge) {
            this.planPayment = new PlanPayment(new PremiumPayment(validTerms, maxMaturityAge));
            return this;
        }

        public PlanBuilder withMaturityAmount(int maturityYear, BigDecimal guaranteedSurvivalBenefitAmount) {
            this.maturityAmountSet.add(new MaturityAmount(maturityYear, guaranteedSurvivalBenefitAmount));
            return this;
        }

        public PlanBuilder withCoverages(Set<PlanCoverage> coverages) {
            this.coverages = coverages;
            return this;
        }

        public Plan build() {
            return new Plan(this);
        }
    }


}
