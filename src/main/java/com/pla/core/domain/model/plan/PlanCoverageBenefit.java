package com.pla.core.domain.model.plan;

import com.google.common.base.Preconditions;
import com.pla.sharedkernel.domain.model.CoverageBenefitDefinition;
import com.pla.sharedkernel.domain.model.CoverageBenefitType;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
@ToString
@Getter
@EqualsAndHashCode(of = {"benefitId"})
public class PlanCoverageBenefit {

    private BenefitId benefitId;
    private CoverageBenefitDefinition definedPer;
    private CoverageBenefitType coverageBenefitType;
    private BigDecimal benefitLimit;
    private BigDecimal maxLimit;
    private CoverageId coverageId;
    private String coverageName;
    private String benefitName;

    private  Long waitingPeriod;

    PlanCoverageBenefit() {
    }

    PlanCoverageBenefit(CoverageId coverageId, String coverageName,
                        String benefitName,
                        String benefitId, CoverageBenefitDefinition definedPer,
                        CoverageBenefitType coverageBenefitType,
                        BigDecimal benefitLimit, BigDecimal maxLimit,Long waitingPeriod) {

        Preconditions.checkArgument(benefitId != null, "Expected benefitId!=null, but %s!=null", benefitId);
        Preconditions.checkArgument(definedPer != null, "Expected definedPer!=null, but %s!=null.", definedPer);
        Preconditions.checkArgument(coverageBenefitType != null, "Expected coverageBenefitType!=null, but %s!=null.", coverageBenefitType);
        Preconditions.checkArgument(benefitLimit != null, "Expected limit!=null, but %s!=null.", benefitLimit);
        this.coverageId = coverageId;
        this.coverageName = coverageName;
        this.benefitName = benefitName;
        this.benefitId = new BenefitId(benefitId);
        this.definedPer = definedPer;
        this.coverageBenefitType = coverageBenefitType;
        this.benefitLimit = benefitLimit;
        this.maxLimit = maxLimit;
        this.waitingPeriod=waitingPeriod;
    }


    public boolean isValidBenefitLimit(BigDecimal benefitLimit) {
        return benefitLimit.equals(this.benefitLimit) || (benefitLimit.compareTo(this.maxLimit) == -1 || benefitLimit.compareTo(this.maxLimit) == 0);
    }

}
