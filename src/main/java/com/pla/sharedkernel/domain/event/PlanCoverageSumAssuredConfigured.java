package com.pla.sharedkernel.domain.event;

import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 21/03/2015
 */
@Getter
public class PlanCoverageSumAssuredConfigured extends SumAssuredConfigured {
    //These attributes capture the Sum Assured Configuration for Coverage.
    private CoverageId coverageId;
    //TODO Check if really the percentage has to be a whole number
    private int percentage;

    public PlanCoverageSumAssuredConfigured(PlanId planId, CoverageId coverageId, int percentage,
                                            BigDecimal maxSumAssuredAmount) {
        super();
        this.coverageId = coverageId;
        this.percentage = percentage;
        this.maxSumAssuredAmount = maxSumAssuredAmount;
        sumAssuredType = SumAssuredType.DERIVED;
        super.planId = planId;
    }


    public PlanCoverageSumAssuredConfigured(PlanId planId, CoverageId coverageId, BigDecimal minSumAssuredAmount,
                                            BigDecimal maxSumAssuredAmount, int multiplesOf) {
        super(planId, minSumAssuredAmount, maxSumAssuredAmount, multiplesOf);
        this.coverageId = coverageId;
    }


    public PlanCoverageSumAssuredConfigured(PlanId planId, CoverageId coverageId, Set<BigDecimal> assuredValues) {
        super(planId, assuredValues);
        this.coverageId = coverageId;
    }
}
