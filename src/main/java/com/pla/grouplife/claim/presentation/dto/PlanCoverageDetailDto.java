package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.sharedresource.model.vo.CoveragePremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Created by nthdimensioncompany on 28/12/2015.
 */
@NoArgsConstructor
@Getter
@lombok.Setter

public class PlanCoverageDetailDto {

    private String policyNumber;
    private PlanPremiumDetail planPremiumDetail;
    private Set<CoveragePremiumDetail> coveragePremiumDetails;
    private Set<String> claimTypes;
}
