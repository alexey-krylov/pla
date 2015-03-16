package com.pla.core.domain.model.plan;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@EqualsAndHashCode
@Embeddable
class MaturityAmount {

    @Column(nullable = false)
    int maturityYear;
    @Column(nullable = false)
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
