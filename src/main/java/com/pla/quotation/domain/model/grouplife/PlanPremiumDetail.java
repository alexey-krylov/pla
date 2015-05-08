package com.pla.quotation.domain.model.grouplife;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by Samir on 4/29/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
class PlanPremiumDetail {

    private PlanId planId;

    private String planCode;

    private BigDecimal premiumAmount;

    private BigDecimal incomeMultiplier;

    private BigDecimal sumAssured;

    PlanPremiumDetail(PlanId planId, String planCode, BigDecimal premiumAmount) {
        this.planId = planId;
        this.planCode = planCode;
        this.premiumAmount = premiumAmount;
    }
}
