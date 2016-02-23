package com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization;

import lombok.*;

import java.math.BigDecimal;

/**
 * Created by Mohan Sharma on 1/21/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BenefitDetailDto {
    private String benefitName;
    private String benefitCode;
    private BigDecimal probableClaimAmount;
    private BigDecimal approvedAmount;
}
