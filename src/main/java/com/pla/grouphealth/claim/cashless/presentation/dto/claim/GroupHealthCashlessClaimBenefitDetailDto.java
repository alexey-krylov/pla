package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimBenefitDetailDto {
    private String benefitName;
    private String benefitCode;
    private BigDecimal probableClaimAmount;
}
