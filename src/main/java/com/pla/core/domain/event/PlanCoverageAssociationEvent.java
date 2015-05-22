package com.pla.core.domain.event;

import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

/**
 * Created by pradyumna on 21-04-2015.
 */
@Getter
public class PlanCoverageAssociationEvent {
    private final PlanId planId;
    private final String planName;
    private final String planCode;
    private final LineOfBusinessEnum lineOfBusinessId;
    private final ClientType clientType;
    private final PlanType planType;
    private final LocalDate launchDate;
    private final LocalDate withdrawalDate;
    private final boolean funeralCover;

    private final Map<CoverageType, Map<CoverageId, List<BenefitId>>> coverageAndBenefits;

    public PlanCoverageAssociationEvent(PlanId planId, String planName, String planCode, LineOfBusinessEnum lineOfBusinessId,
                                        ClientType clientType, PlanType planType, LocalDate launchDate, LocalDate withdrawalDate, boolean funeralCover,
                                        Map<CoverageType, Map<CoverageId, List<BenefitId>>> coverageAndBenefits) {
        this.planId = planId;
        this.planName = planName;
        this.planCode = planCode;
        this.lineOfBusinessId = lineOfBusinessId;
        this.clientType = clientType;
        this.planType = planType;
        this.launchDate = launchDate;
        this.withdrawalDate = withdrawalDate;
        this.coverageAndBenefits = coverageAndBenefits;
        this.funeralCover = funeralCover;
    }
}
