package com.pla.core.domain.model.plan;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@EqualsAndHashCode
@Getter
class SumAssuredByRange extends SumAssured {
    BigDecimal minSumInsured;
    BigDecimal maxSumInsured;
    int multiplesOf;

    SumAssuredByRange(BigDecimal minSumInsured, BigDecimal maxSumInsured, int multiplesOf) {
        this.minSumInsured = minSumInsured;
        this.maxSumInsured = maxSumInsured;
        this.multiplesOf = multiplesOf;
    }
}