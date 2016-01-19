package com.pla.grouphealth.claim.cashless.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Mohan Sharma on 1/18/2016.
 */
@NoArgsConstructor
@Getter
public class PreAuthorizationRequestBenefitDetail {
    private String benefitName;
    private String benefitId;
    private BigDecimal probableClaimAmount;
}
