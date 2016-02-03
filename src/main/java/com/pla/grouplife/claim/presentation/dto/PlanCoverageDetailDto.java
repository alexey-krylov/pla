package com.pla.grouplife.claim.presentation.dto;

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
    private PlanDetailDto planDetailDto;
    private Set<CoverageDetailDto> coverageDetailDtos;
    private Set<String> claimTypes;
}
