package com.pla.core.domain.model.plan;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@EqualsAndHashCode
class MaturityAmount {

    int maturityYear;
    BigDecimal guaranteedSurvivalBenefitAmount;

    protected MaturityAmount() {
    }
    /**
     * The maturity year would be from the policy inception date.
     *
     * @param maturityYear
     * @param guaranteedSurvivalBenefitAmount
     */
    MaturityAmount(int maturityYear, BigDecimal guaranteedSurvivalBenefitAmount) {
        this.maturityYear = maturityYear;
        this.guaranteedSurvivalBenefitAmount = guaranteedSurvivalBenefitAmount;
    }
}
