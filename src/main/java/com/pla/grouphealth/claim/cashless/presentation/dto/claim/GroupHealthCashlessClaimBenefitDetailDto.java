package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBenefitDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimBenefitDetailDto {
    private String benefitName;
    private String benefitCode;
    private BigDecimal probableClaimAmount;
    private BigDecimal preAuthorizationAmount;
    private BigDecimal eligibleAmount;
    private BigDecimal approvedAmount;

    public GroupHealthCashlessClaimBenefitDetailDto updateWithDetails(GroupHealthCashlessClaimBenefitDetail benefit) {
        if(isNotEmpty(benefit)){
            this.benefitName = benefit.getBenefitName();
            this.benefitCode = benefit.getBenefitCode();
            this.preAuthorizationAmount = benefit.getPreAuthorizationAmount();
            this.probableClaimAmount = benefit.getProbableClaimAmount();
            this.eligibleAmount = benefit.getEligibleAmount();
            this.approvedAmount = benefit.getApprovedAmount();
        }
        return this;
    }

    public GroupHealthCashlessClaimBenefitDetailDto updateWithEligibleAmount(BigDecimal balanceAmount) {
        if(balanceAmount.compareTo(this.probableClaimAmount) == 1 || balanceAmount.compareTo(this.probableClaimAmount) == 0){
            this.eligibleAmount = this.probableClaimAmount;
        }
        if(balanceAmount.compareTo(this.probableClaimAmount) == -1){
            this.eligibleAmount = balanceAmount;
        }
        return this;
    }

}
