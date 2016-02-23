package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimBenefitDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;

import static org.nthdimenzion.utils.UtilValidator.*;

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
    private BigDecimal eligibleAmount;
    private BigDecimal approvedAmount;

    public GroupHealthCashlessClaimBenefitDetail updateWithEligibleAmount(BigDecimal balanceAmount) {
        if(balanceAmount.compareTo(this.probableClaimAmount) == 1 || balanceAmount.compareTo(this.probableClaimAmount) == 0){
            this.eligibleAmount = this.probableClaimAmount;
        }
        if(balanceAmount.compareTo(this.probableClaimAmount) == -1){
            this.eligibleAmount = balanceAmount;
        }
        return this;
    }

    public GroupHealthCashlessClaimBenefitDetail updateWithDetails(GroupHealthCashlessClaimBenefitDetailDto benefit) {
        if(isNotEmpty(benefit)){
            this.benefitName = benefit.getBenefitName();
            this.benefitCode = benefit.getBenefitCode();
            this.probableClaimAmount = benefit.getProbableClaimAmount();
            this.preAuthorizationAmount = benefit.getPreAuthorizationAmount();
            this.eligibleAmount = benefit.getEligibleAmount();
            this.approvedAmount = benefit.getApprovedAmount();
        }
        return this;
    }
}
