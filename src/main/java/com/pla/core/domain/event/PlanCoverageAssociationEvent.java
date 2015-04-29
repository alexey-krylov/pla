package com.pla.core.domain.event;

import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Created by pradyumna on 21-04-2015.
 */
@Getter
public class PlanCoverageAssociationEvent {
    private final PlanId planId;
    private final Map<CoverageType, Map<CoverageId, List<BenefitId>>> coverageAndBenefits;

    public PlanCoverageAssociationEvent(PlanId planId, Map<CoverageType, Map<CoverageId, List<BenefitId>>> coverageAndBenefits) {
        this.planId = planId;
        this.coverageAndBenefits = coverageAndBenefits;
    }
}
