package com.pla.grouphealth.claim.cashless.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Author - Mohan Sharma Created on 1/11/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PreAuthorizationRequestCoverageDetail {
    String coverageCode;
    String coverageName;
    BigDecimal sumAssured;
    private Set<PreAuthorizationRequestBenefitDetail> benefitDetails;
    private BigDecimal totalAmountPaid;
    private BigDecimal balanceAmount;
    private BigDecimal reserveAmount;
    private BigDecimal eligibleAmount;
    private BigDecimal approvedAmount;

}
