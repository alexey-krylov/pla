package com.pla.grouplife.claim.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by ak
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class ApproverCoverageDetail {

    private String coverageName;
    private BigDecimal sumAssured;
    private BigDecimal approvedAmount;
    private BigDecimal amendedAmount;
    private BigDecimal additionalAmount;
    private String recoveryOrAdditional;
    private String remarks;
}
