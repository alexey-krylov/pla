package com.pla.core.domain.model.plan;

import com.google.common.base.Preconditions;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@Getter
@EqualsAndHashCode
public class SumAssured {

    private SortedSet<BigDecimal> sumAssuredValue = new TreeSet<BigDecimal>();
    //These attributes capture the Sum Assured Configuration for Coverage.
    private CoverageId coverageId;
    //TODO Check if really the percentage has to be a whole number
    private int percentage;
    private BigInteger maxLimit;

    private BigDecimal minSumInsured;
    private BigDecimal maxSumInsured;
    private int multiplesOf;
    private SumAssuredType sumAssuredType;

    SumAssured() {

    }

    SumAssured(SortedSet<BigDecimal> sumAssuredValues) {
        checkArgument(UtilValidator.isNotEmpty(sumAssuredValues));
        this.sumAssuredValue = sumAssuredValues;
        this.sumAssuredType = SumAssuredType.SPECIFIED_VALUES;
    }


    SumAssured(BigDecimal minSumInsured, BigDecimal maxSumInsured, int multiplesOf) {

        checkArgument(minSumInsured != null, "Min Sum Insured Amount is required.");
        checkArgument(maxSumInsured != null, "Max Sum Insured Amount is required.");
        checkArgument(multiplesOf % 10 == 0, "It has to be multiples of 10.");
        checkArgument(maxSumInsured.compareTo(minSumInsured) == 1, "Expected maxSumInsured > minSumInsured, but %s<%s", maxSumInsured, minSumInsured);
        checkArgument(maxSumInsured.compareTo(BigDecimal.ZERO) == 1,
                "MinSumAssuredAmount greater than zero Expected, but got %d", minSumInsured);
        checkArgument(maxSumInsured.compareTo(BigDecimal.ZERO) == 1,
                "MaxSumAssuredAmount greater than zero Expected, but got %d", minSumInsured);
        checkArgument(maxSumInsured.compareTo(minSumInsured) == 1,
                "MaxSumAssuredAmount>MinSumAssuredAmount Expected, but %d>%d",
                maxSumInsured, minSumInsured);
        checkArgument(multiplesOf % 10 == 0, " Not valid Multiples.");
        this.minSumInsured = minSumInsured;
        this.maxSumInsured = maxSumInsured;
        this.multiplesOf = multiplesOf;
        this.sumAssuredType = SumAssuredType.RANGE;
    }

    /**
     * The coverage id is mandatory for creating the derivedSumAssured
     * Even if it is an instance of SumAssured it cannot be configured
     * for Plan.
     *
     * @param coverageId
     * @param percentage
     * @param maxLimit
     */
    SumAssured(CoverageId coverageId, int percentage, BigInteger maxLimit) {
        Preconditions.checkArgument(coverageId != null);
        Preconditions.checkArgument(percentage > 0);
        Preconditions.checkArgument(maxLimit.compareTo(BigInteger.ZERO) == 1);
        this.coverageId = coverageId;
        this.percentage = percentage;
        this.maxLimit = maxLimit;
        this.sumAssuredType = SumAssuredType.DERIVED;
    }

}
