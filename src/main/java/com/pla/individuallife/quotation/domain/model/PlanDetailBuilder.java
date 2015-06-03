package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Set;

/**
 * Created by Karunakar on 5/25/2015.
 */
@Getter
public class PlanDetailBuilder {

    private PlanId planId;

    private Integer policyTerm;

    private Integer premiumPaymentTerm;

    private BigInteger sumAssured;

    private Set<RiderDetail> riderDetails;

    public PlanDetailBuilder withRiderDetails(Set<RiderDetail> riderDetails) {
        this.riderDetails = riderDetails;
        return this;
    }

    public PlanDetailBuilder withPlanId(PlanId planId) {
        this.planId = planId;
        return this;
    }

    public PlanDetailBuilder withPolicyTerm(Integer policyTerm) {
        this.policyTerm = policyTerm;
        return this;
    }

    public PlanDetailBuilder withPremiumPaymentTerm(Integer premiumPaymentTerm) {
        this.premiumPaymentTerm = premiumPaymentTerm;
        return this;
    }

    public PlanDetailBuilder withSumAssured(BigInteger sumAssured) {
        this.sumAssured = sumAssured;
        return this;
    }

    public PlanDetail build() {
        return new PlanDetail(this);
    }


}
