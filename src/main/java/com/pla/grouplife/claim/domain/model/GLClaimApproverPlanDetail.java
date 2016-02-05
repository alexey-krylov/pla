package com.pla.grouplife.claim.domain.model;

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
public class GLClaimApproverPlanDetail {

    private String  planName;
    private BigDecimal planSumAssured;
    private BigDecimal approvedAmount;
    private BigDecimal  amendedAmount;

}
