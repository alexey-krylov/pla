package com.pla.core.domain.model.plan;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@Getter
@DiscriminatorValue(value = "SUM_ASSURED_RANGE")
class SumAssuredByRange extends SumAssured {
    BigDecimal minSumInsured;
    BigDecimal maxSumInsured;
    int multiplesOf;

    SumAssuredByRange(BigDecimal minSumInsured, BigDecimal maxSumInsured, int multiplesOf) {
        checkArgument(minSumInsured != null, "Min Sum Insured Amount is required.");
        checkArgument(maxSumInsured != null, "Max Sum Insured Amount is required.");
        checkArgument(multiplesOf % 10 == 0, "It has to be multiples of 10.");
        checkArgument(maxSumInsured.compareTo(minSumInsured) == 1, "Expected maxSumInsured > minSumInsured, but %s<%s", maxSumInsured, minSumInsured);
        this.minSumInsured = minSumInsured;
        this.maxSumInsured = maxSumInsured;
        this.multiplesOf = multiplesOf;
    }
}