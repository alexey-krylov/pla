package com.pla.grouphealth.claim.cashless.domain.model.preauthorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Mohan Sharma on 1/18/2016.
 */
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PreAuthorizationRequestBenefitDetail {
    private String benefitName;
    private String benefitCode;
    private BigDecimal probableClaimAmount;
}
