package com.pla.individuallife.sharedresource.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Karunakar on 5/26/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class PlanDetailDto {

    private String planId;

    private Integer policyTerm;

    private String premiumPaymentType;

    private Integer premiumPaymentTerm;

    private BigDecimal sumAssured;

    private Set<RiderDetailDto> riderDetails;

    private BigDecimal annualFee = BigDecimal.ZERO;

    private BigDecimal semiAnnualFee = BigDecimal.ZERO;

    private BigDecimal monthlyFee = BigDecimal.ZERO;

    private BigDecimal quarterlyFee = BigDecimal.ZERO;
}
