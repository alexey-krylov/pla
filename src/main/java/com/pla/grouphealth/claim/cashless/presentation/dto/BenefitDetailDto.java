package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Mohan Sharma on 1/21/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class BenefitDetailDto {
    private String benefitName;
    private String benefitCode;
    private BigDecimal probableClaimAmount;
}
