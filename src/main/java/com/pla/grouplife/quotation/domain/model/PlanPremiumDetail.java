package com.pla.grouplife.quotation.domain.model;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

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

    PlanPremiumDetail(PlanId planId, String planCode, BigDecimal premiumAmount,BigDecimal sumAssured) {
        checkArgument(planId != null);
        checkArgument(isNotEmpty(planCode));
        checkArgument(premiumAmount != null);
        checkArgument(sumAssured != null);
        this.planId = planId;
        this.planCode = planCode;
        this.premiumAmount = premiumAmount;
        this.sumAssured=sumAssured;
    }

    public PlanPremiumDetail updatePremiumAmount(BigDecimal premiumAmount){
        this.premiumAmount=premiumAmount;
        return this;
    }
}
