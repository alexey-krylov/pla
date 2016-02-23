package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBenefitDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimCoverageDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.*;

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
    private BigDecimal deductibleAmount;
    private BigDecimal deductiblePercentage;
    private String deductibleType;

    public GroupHealthCashlessClaimCoverageDetailDto updateWithDetails(GroupHealthCashlessClaimCoverageDetail coverage) {
        if(isNotEmpty(coverage)){
            this.coverageId = coverage.getCoverageId();
            this.coverageCode = coverage.getCoverageCode();
            this.coverageName = coverage.getCoverageName();
            this.sumAssured = coverage.getSumAssured();
            this.totalAmountPaid = coverage.getTotalAmountPaid();
            this.balanceAmount = coverage.getBalanceAmount();
            this.reserveAmount = coverage.getReserveAmount();
            this.deductibleAmount = coverage.getDeductibleAmount();
            this.deductiblePercentage = coverage.getDeductiblePercentage();
            this.deductibleType = coverage.getDeductibleType();
            this.benefitDetails = constructBenefitDetails(coverage.getBenefitDetails());
        }
        return this;
    }

    private Set<GroupHealthCashlessClaimBenefitDetailDto> constructBenefitDetails(Set<GroupHealthCashlessClaimBenefitDetail> benefitDetails) {
        return isNotEmpty(benefitDetails) ? benefitDetails.stream().map(benefit -> new GroupHealthCashlessClaimBenefitDetailDto().updateWithDetails(benefit)).collect(Collectors.toSet()) : Sets.newHashSet();
    }


    public BigDecimal getTotalProbableClaimAmount() {
        BigDecimal totalProbableClaimAmount = BigDecimal.ZERO;
        for(GroupHealthCashlessClaimBenefitDetailDto benefit : this.getBenefitDetails()){
            totalProbableClaimAmount = totalProbableClaimAmount.add(benefit.getProbableClaimAmount());
        }
        return totalProbableClaimAmount;
    }


    public BigDecimal getSumOfTotalApprovedAmount() {
        BigDecimal totalAmountPaidWithoutCurrentApproveAmount = BigDecimal.ZERO;
        for(GroupHealthCashlessClaimBenefitDetailDto benefit : this.getBenefitDetails()){
            if(isNotEmpty(benefit.getApprovedAmount())){
                totalAmountPaidWithoutCurrentApproveAmount = totalAmountPaidWithoutCurrentApproveAmount.add(benefit.getApprovedAmount());
            }
        }
        return totalAmountPaidWithoutCurrentApproveAmount;
    }

    public GroupHealthCashlessClaimCoverageDetailDto updateWithProbableClaimAmount(String coverageId, Set<GroupHealthCashlessClaimBenefitDetailDto> benefitDetails, List<Map<String, Object>> finalRefurbishedList) {
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

    public GroupHealthCashlessClaimCoverageDetailDto updateWithBalanceAmount() {
        BigDecimal sumAssured = this.sumAssured;
        BigDecimal totalAmountPaid = this.totalAmountPaid;
        BigDecimal reservedAmount = this.reserveAmount;
        if(sumAssured.compareTo(totalAmountPaid) == 1){
            BigDecimal balanceAmount = sumAssured.subtract(totalAmountPaid);
            this.balanceAmount = balanceAmount;
        }
        if(sumAssured.compareTo(totalAmountPaid) == 0 || sumAssured.compareTo(totalAmountPaid) == -1){
            this.balanceAmount = BigDecimal.ZERO;
        }
        return this;
    }

    public GroupHealthCashlessClaimCoverageDetailDto updateWithEligibleAmount() {
        BigDecimal totalProbableClaimAmount = getTotalProbableClaimAmount();
        if(isNotEmpty(this.deductibleType) && this.deductibleType.equals("PERCENTAGE")){
            BigDecimal deductibleAmount = getPercentageValue(totalProbableClaimAmount, this.deductiblePercentage);
            this.deductibleAmount = deductibleAmount;
        }
        this.benefitDetails.stream().map(benefit -> benefit.updateWithEligibleAmount(this.balanceAmount)).collect(Collectors.toList());
        return this;
    }

    public BigDecimal getPercentageValue(BigDecimal base, BigDecimal pct){
        if(isNotEmpty(base))
            return base.multiply(pct).divide(new BigDecimal(100));
        return BigDecimal.ZERO;
    }

    public GroupHealthCashlessClaimCoverageDetailDto updateWithReserveAmount(BigDecimal reservedAmount) {
        this.reserveAmount = reservedAmount;
        return this;
    }
}
