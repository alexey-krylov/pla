package com.pla.sharedkernel.domain.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@EqualsAndHashCode
public class MaturityAmount {

    int maturityYear;
    BigDecimal guaranteedSurvivalBenefitAmount;

    /**
     * The maturity year would be from the policy inception date.
     *
     * @param maturityYear
     * @param guaranteedSurvivalBenefitAmount
     */
    public MaturityAmount(int maturityYear, BigDecimal guaranteedSurvivalBenefitAmount) {
        this.maturityYear = maturityYear;
        this.guaranteedSurvivalBenefitAmount = guaranteedSurvivalBenefitAmount;
    }
}
