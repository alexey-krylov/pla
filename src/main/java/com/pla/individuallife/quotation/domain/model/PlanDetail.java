package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Created by Karunakar on 5/25/2015.
 */
@Embeddable
@Getter
@Setter
public class PlanDetail {

    private PlanId planId;

    private Integer policyTerm;

    private String premiumPaymentType;

    private Integer premiumPaymentTerm;

    private BigDecimal sumAssured;

    private BigDecimal annualPremium;

    private BigDecimal semiannualPremium;

    private BigDecimal quarterlyPremium;

    private BigDecimal monthlyPremium;

    private BigDecimal totalPremium;

    PlanDetail() {
    }

    public PlanDetail(PlanId planId, Integer policyTerm, Integer premiumPaymentTerm, BigDecimal sumAssured) {
        this.planId = planId;
        this.policyTerm = policyTerm;
        this.premiumPaymentTerm = premiumPaymentTerm;
        this.sumAssured = sumAssured;
    }

}
