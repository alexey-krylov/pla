package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.math.BigInteger;

/**
 * Created by Karunakar on 5/25/2015.
 */
@Embeddable
@Getter
public class PlanDetail {

    private PlanId planId;

    private Integer policyTerm;

    private Integer premiumPaymentTerm;

    private BigInteger sumAssured;

    PlanDetail() {
    }

    public PlanDetail(PlanId planId, Integer policyTerm, Integer premiumPaymentTerm, BigInteger sumAssured) {
        this.planId = planId;
        this.policyTerm = policyTerm;
        this.premiumPaymentTerm = premiumPaymentTerm;
        this.sumAssured = sumAssured;
    }
}
