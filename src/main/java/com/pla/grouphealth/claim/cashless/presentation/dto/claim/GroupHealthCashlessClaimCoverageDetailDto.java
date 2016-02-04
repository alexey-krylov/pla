package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBenefitDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimCoverageDetailDto {
    private String coverageId;
    private String coverageCode;
    private String coverageName;
    private BigDecimal sumAssured;
    private Set<GroupHealthCashlessClaimBenefitDetailDto> benefitDetails;
    private BigDecimal totalAmountPaid;
    private BigDecimal balanceAmount;
    private BigDecimal reserveAmount;
    private BigDecimal eligibleAmount;
    private BigDecimal approvedAmount;
}
