package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.exception.ClaimNotEligibleException;
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
    private BigDecimal additionalAmount;
    private BigDecimal recoveryAmount;
    private BigDecimal approvedAmount = BigDecimal.ZERO;

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
        if(isNotEmpty(this.approvedAmount) && isNotEmpty(this.eligibleAmount)){
            if(this.eligibleAmount.compareTo(this.approvedAmount) == 1)
                this.eligibleAmount = this.eligibleAmount.subtract(this.approvedAmount);
            else
                this.eligibleAmount = BigDecimal.ZERO;
        }
        return this;
    }

    public GroupHealthCashlessClaimBenefitDetailDto updateWithApprovedAmount() throws ClaimNotEligibleException {
        if(isNotEmpty(this.additionalAmount))
            this.approvedAmount = this.approvedAmount.add(this.additionalAmount);
        if(isNotEmpty(this.recoveryAmount)){
            if(approvedAmount.compareTo(this.recoveryAmount) == 1 || approvedAmount.compareTo(this.recoveryAmount) == 0)
                approvedAmount = approvedAmount.subtract(this.recoveryAmount);
            else {
                throw new ClaimNotEligibleException("Recovery amount cannot be greater than approved amount");
            }
        }
        return this;
    }
}
