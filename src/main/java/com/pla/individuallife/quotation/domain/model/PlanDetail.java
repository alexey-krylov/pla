package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Created by Karunakar on 5/25/2015.
 */
@Embeddable
@Getter
public class PlanDetail {
    private PlanId planId;

    private Integer policyTerm;

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

    public void setAnnualPremium(BigDecimal annualPremium) {
        this.annualPremium = annualPremium;
    }

    public void setSemiannualPremium(BigDecimal semiannualPremium) {
        this.semiannualPremium = semiannualPremium;
    }

    public void setQuarterlyPremium(BigDecimal quarterlyPremium) {
        this.quarterlyPremium = quarterlyPremium;
    }

    public void setMonthlyPremium(BigDecimal monthlyPremium) {
        this.monthlyPremium = monthlyPremium;
    }

    public void setTotalPremium(BigDecimal totalPremium) {
        this.totalPremium = totalPremium;
    }
}
