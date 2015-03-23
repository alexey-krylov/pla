package com.pla.sharedkernel.domain.event;

import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author: pradyumna
 * @since 1.0 18/03/2015
 */
@Getter
public class SumAssuredConfigured {

    protected SumAssuredType sumAssuredType;
    protected BigDecimal maxSumAssuredAmount;
    protected PlanId planId;
    private SortedSet<BigDecimal> assuredValues = new TreeSet<BigDecimal>();
    private BigDecimal minSumAssuredAmount;
    private int multiplesOf;

    protected SumAssuredConfigured() {
    }

    public SumAssuredConfigured(PlanId planId, BigDecimal minSumAssuredAmount,
                                BigDecimal maxSumAssuredAmount, int multiplesOf) {
        this.planId = planId;
        this.minSumAssuredAmount = minSumAssuredAmount;
        this.maxSumAssuredAmount = maxSumAssuredAmount;
        this.multiplesOf = multiplesOf;
        sumAssuredType = SumAssuredType.RANGE;
    }


    public SumAssuredConfigured(PlanId planId, Set<BigDecimal> assuredValues) {
        this.planId = planId;
        this.assuredValues = new TreeSet<BigDecimal>(assuredValues);
        sumAssuredType = SumAssuredType.SPECIFIED_VALUES;
    }
}
