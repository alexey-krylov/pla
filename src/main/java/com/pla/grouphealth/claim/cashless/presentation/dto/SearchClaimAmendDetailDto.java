package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimAssuredDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimCoverageDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimPolicyDetail;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 2/19/2016.
 */
@Data
public class SearchClaimAmendDetailDto {
    private String policyHolderName;
    private String claimNumber;
    private String clientId;
    private String salutation;
    private String assuredFirstName;
    private String assuredLastName;
    private String policyNumber;
    private String assuredNRCNumber;
    private String status;
    private LocalDate approvedOn;
    private BigDecimal approvedAmount;
    private boolean showModalWin;
    private String errorMessage;

    public SearchClaimAmendDetailDto updateWithDetails(GroupHealthCashlessClaim claim) {
        if(isNotEmpty(claim)){
            this.policyHolderName = isNotEmpty(claim.getGhProposer()) ? claim.getGhProposer().getProposerName() : StringUtils.EMPTY;
            this.claimNumber = claim.getGroupHealthCashlessClaimId();
            this.status = claim.getStatus().getDescription();
            this.approvedOn = claim.getApprovedOnDate();
            GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail = claim.getGroupHealthCashlessClaimPolicyDetail();
            if(isNotEmpty(groupHealthCashlessClaimPolicyDetail)){
                this.approvedAmount = getSumOfAllApprovedAmount(groupHealthCashlessClaimPolicyDetail.getCoverageDetails());
                this.policyNumber = isNotEmpty(groupHealthCashlessClaimPolicyDetail.getPolicyNumber()) ? groupHealthCashlessClaimPolicyDetail.getPolicyNumber().getPolicyNumber() : StringUtils.EMPTY;
                GroupHealthCashlessClaimAssuredDetail assuredDetail = groupHealthCashlessClaimPolicyDetail.getAssuredDetail();
                if(isNotEmpty(assuredDetail)){
                    this.clientId = assuredDetail.getClientId();
                    this.salutation = assuredDetail.getSalutation();
                    this.assuredFirstName = assuredDetail.getFirstName();
                    this.assuredLastName = assuredDetail.getSurname();
                    this.assuredNRCNumber = assuredDetail.getNrcNumber();
                }
            }
        }
        return this;
    }

    private BigDecimal getSumOfAllApprovedAmount(Set<GroupHealthCashlessClaimCoverageDetail> coverageDetails) {
        BigDecimal totalApprovedAmount = BigDecimal.ZERO;
        if(isNotEmpty(coverageDetails)){
            for(GroupHealthCashlessClaimCoverageDetail coverageDetail : coverageDetails){
                totalApprovedAmount = totalApprovedAmount.add(coverageDetail.getSumOfTotalApprovedAmount());
            }
        }
        return totalApprovedAmount;
    }

    public SearchClaimAmendDetailDto updateWithShowModalWin() {
        this.showModalWin = Boolean.TRUE;
        return this;
    }

    public SearchClaimAmendDetailDto updateWithErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
