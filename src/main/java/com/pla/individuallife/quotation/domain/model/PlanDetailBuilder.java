package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 5/25/2015.
 */
@Getter
public class PlanDetailBuilder {

    private String planDetailId;

    private PlanId planId;

    private Integer policyTerm;

    private Integer premiumPaymentTerm;

    private BigInteger sumAssured;

    private Set<RiderDetail> riderDetails;

    public void withRiderDetails(Set<RiderDetail> riderDetails) {
        this.riderDetails = riderDetails;
    }

    public void withPlanDetailId(String planDetailId) {
        this.planDetailId = planDetailId;
    }

    public void withPlanId(PlanId planId) {
        this.planId = planId;
    }

    public void withPolicyTerm(Integer policyTerm) {
        this.policyTerm = policyTerm;
    }

    public void withPremiumPaymentTerm(Integer premiumPaymentTerm) {
        this.premiumPaymentTerm = premiumPaymentTerm;
    }

    public void withSumAssured(BigInteger sumAssured) {
        this.sumAssured = sumAssured;
    }

    PlanDetailBuilder(String planDetailId, PlanId planId, Integer policyTerm, Integer premiumPaymentTerm, BigInteger sumAssured, Set<RiderDetail> riderDetails) {
        checkArgument(isNotEmpty(planDetailId));
        checkArgument(planId != null);
        checkArgument(premiumPaymentTerm != null);
        checkArgument(policyTerm != null);
        checkArgument(sumAssured != null );
        this.planDetailId = planDetailId;
        this.planId = planId;
        this.policyTerm = policyTerm;
        this.premiumPaymentTerm = premiumPaymentTerm;
        this.sumAssured = sumAssured;
        this.riderDetails = riderDetails;
    }

    public PlanDetail build() {
        return new PlanDetail(this);
    }

}
