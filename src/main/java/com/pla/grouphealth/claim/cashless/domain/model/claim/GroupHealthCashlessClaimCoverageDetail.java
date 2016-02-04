package com.pla.grouphealth.claim.cashless.domain.model.claim;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    private String coverageId;
    private String coverageCode;
    private String coverageName;
    private BigDecimal sumAssured;
    private Set<GroupHealthCashlessClaimBenefitDetail> benefitDetails;
    private BigDecimal totalAmountPaid;
    private BigDecimal balanceAmount;
    private BigDecimal reserveAmount;
    private BigDecimal eligibleAmount;
    private BigDecimal approvedAmount;

    public GroupHealthCashlessClaimCoverageDetail updateWithCoverageName(String coverageName) {
        this.coverageName = coverageName;
        return this;
    }

    public GroupHealthCashlessClaimCoverageDetail updateWithCoverageId(String coverageId) {
        this.coverageId = coverageId;
        return this;
    }

    public GroupHealthCashlessClaimCoverageDetail updateWithCoverageCode(String coverageCode) {
        this.coverageCode = coverageCode;
        return this;
    }

    public GroupHealthCashlessClaimCoverageDetail updateWithSumAssured(BigDecimal sumAssured) {
        this.sumAssured = sumAssured;
        return this;
    }

    public GroupHealthCashlessClaimCoverageDetail updateWithTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
        return this;
    }

    public GroupHealthCashlessClaimCoverageDetail updateWithReserveAmount(BigDecimal reservedAmount) {
        this.reserveAmount = reservedAmount;
        return this;
    }

    public GroupHealthCashlessClaimCoverageDetail updateWithBalanceAndEligibleAmount() {
        BigDecimal sumAssured = this.sumAssured;
        BigDecimal totalAmountPaid = this.totalAmountPaid;
        if(sumAssured.compareTo(totalAmountPaid) == 1){
            BigDecimal balanceAmount = sumAssured.subtract(totalAmountPaid);
            this.balanceAmount = balanceAmount;
            this.eligibleAmount = balanceAmount;
        }
        if(sumAssured.compareTo(totalAmountPaid) == 0){
            this.balanceAmount = BigDecimal.ZERO;
            this.eligibleAmount = BigDecimal.ZERO;
        }
        return this;
    }

    public GroupHealthCashlessClaimCoverageDetail updateWithBenefitDetails(Set<GroupHealthCashlessClaimBenefitDetail> benefitDetails) {
        this.benefitDetails = benefitDetails;
        return this;
    }

    public GroupHealthCashlessClaimCoverageDetail updateWithProbableClaimAmount(String coverageId, Set<GroupHealthCashlessClaimBenefitDetail> benefitDetails, List<Map<String, Object>> finalRefurbishedList) {
        if(isNotEmpty(benefitDetails)){
            benefitDetails.stream().forEach(benefitDto -> {
                BigDecimal probableClaimAmount = getProbableClaimAmount(benefitDto.getBenefitCode(), coverageId, finalRefurbishedList);
                benefitDto.setProbableClaimAmount(probableClaimAmount);
            });
        }
        return this;
    }

    private BigDecimal getProbableClaimAmount(String benefitCode, String coverageId, List<Map<String, Object>> finalRefurbishedList) {
        BigDecimal probableClaimAmount = BigDecimal.ZERO;
        for(Map<String, Object> map : finalRefurbishedList) {
            String coverageIdFromMap = map.get("coverageId").toString();
            String benefitCodeIdFromMap = map.get("benefitCode").toString();
            if (benefitCode.equalsIgnoreCase(benefitCodeIdFromMap) && coverageId.equalsIgnoreCase(coverageIdFromMap)) {
                probableClaimAmount = (BigDecimal)map.get("payableAmount");
            }
        }
        return probableClaimAmount;
    }
}
