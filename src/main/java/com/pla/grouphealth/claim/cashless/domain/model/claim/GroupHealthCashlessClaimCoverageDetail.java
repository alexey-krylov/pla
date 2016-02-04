package com.pla.grouphealth.claim.cashless.domain.model.claim;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 1/11/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class GroupHealthCashlessClaimCoverageDetail {
    private String coverageCode;
    private String coverageName;
    private BigDecimal sumAssured;
    private Set<GroupHealthCashlessClaimBenefitDetail> benefitDetails;
    private BigDecimal totalAmountPaid;
    private BigDecimal balanceAmount;
    private BigDecimal reserveAmount;
    private BigDecimal eligibleAmount;
    private BigDecimal approvedAmount;
    private String coverageId;

    /*public void updateWithCoverageDetails(CoverageBenefitDetailDto coverageBenefitDetailDto) {
        if(isNotEmpty(coverageBenefitDetailDto)){
            this.coverageCode = coverageBenefitDetailDto.getCoverageCode();
            this.coverageId = coverageBenefitDetailDto.getCoverageId();
            this.coverageName = coverageBenefitDetailDto.getCoverageName();
            this.sumAssured = coverageBenefitDetailDto.getSumAssured();
            this.totalAmountPaid = coverageBenefitDetailDto.getTotalAmountPaid();
            this.balanceAmount = coverageBenefitDetailDto.getBalanceAmount();
            this.reserveAmount = coverageBenefitDetailDto.getReserveAmount();
            this.eligibleAmount = coverageBenefitDetailDto.getEligibleAmount();
            this.approvedAmount = coverageBenefitDetailDto.getApprovedAmount();
            this.benefitDetails = populateBenefitDetails(coverageBenefitDetailDto.getBenefitDetails());
        }
    }*/
/*
    private Set<PreAuthorizationRequestBenefitDetail> populateBenefitDetails(Set<BenefitDetailDto> benefitDetails) {
        return isNotEmpty(benefitDetails) ? benefitDetails.stream().map(benefit -> {
            return new PreAuthorizationRequestBenefitDetail(benefit.getBenefitName(), benefit.getBenefitCode(), benefit.getProbableClaimAmount());
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }*/
}
