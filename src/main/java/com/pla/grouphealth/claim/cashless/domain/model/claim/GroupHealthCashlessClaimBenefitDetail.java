package com.pla.grouphealth.claim.cashless.domain.model.claim;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Mohan Sharma on 1/18/2016.
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class GroupHealthCashlessClaimBenefitDetail {
    private String benefitName;
    private String benefitCode;
    private BigDecimal probableClaimAmount;
    private BigDecimal preAuthorizationAmount;
}
