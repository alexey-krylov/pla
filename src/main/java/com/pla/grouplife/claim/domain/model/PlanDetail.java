package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mirror on 8/19/2015.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PlanDetail {

    private PlanId planId;

    private BigDecimal sumAssured;

    private List<CoverageDetail> coverageDetails;


    public PlanDetail(PlanId planId, BigDecimal sumAssured, List<CoverageDetail> coverageDetails) {
        this.planId = planId;
        this.coverageDetails = coverageDetails;
    }

    class CoverageDetail {

        private CoverageId coverageId;

        private BigDecimal sumAssured;

        CoverageDetail(CoverageId coverageId, BigDecimal sumAssured) {
            this.coverageId = coverageId;
            this.sumAssured = sumAssured;
        }
    }
}
