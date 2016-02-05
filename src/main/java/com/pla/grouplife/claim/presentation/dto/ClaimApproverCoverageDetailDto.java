package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by ak on 4/2/2016.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ClaimApproverCoverageDetailDto {
    private String coverageName;
    private BigDecimal sumAssured;
    private BigDecimal approvedAmount;
    private BigDecimal amendedAmount;
}
