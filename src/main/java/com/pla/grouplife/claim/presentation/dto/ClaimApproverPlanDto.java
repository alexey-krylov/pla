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

public class ClaimApproverPlanDto {

    private String  planName;
    private BigDecimal planSumAssured;
    private BigDecimal assuredAmount;
    private BigDecimal approvedAmount;
    private BigDecimal  amendedAmount;
}

