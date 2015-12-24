package com.pla.grouplife.sharedresource.model.vo;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Samir on 4/29/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class PlanPremiumDetail {

    private PlanId planId;

    private String planCode;

    private BigDecimal premiumAmount;

    private BigDecimal sumAssured;

    private BigDecimal semiAnnualPremium;

    private BigDecimal quarterlyPremium;

    private BigDecimal monthlyPremium;

    private BigDecimal incomeMultiplier;

    PlanPremiumDetail(PlanId planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured, BigDecimal incomeMultiplier) {
        checkArgument(planId != null);
        this.planId = planId;
        this.planCode = planCode;
        this.premiumAmount = premiumAmount;
        this.sumAssured=sumAssured;
        this.incomeMultiplier = incomeMultiplier;
    }

    public PlanPremiumDetail updatePremiumAmount(BigDecimal premiumAmount){
        this.premiumAmount=premiumAmount;
        return this;
    }

    public PlanPremiumDetail updatePremiumAmount(BigDecimal semiAnnualPremium, BigDecimal quarterlyPremium, BigDecimal monthlyPremium) {
        this.semiAnnualPremium  =semiAnnualPremium;
        this.quarterlyPremium = quarterlyPremium;
        this.monthlyPremium = monthlyPremium;
        return this;
    }
}