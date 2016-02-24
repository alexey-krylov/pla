package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimAssuredDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimPolicyDetail;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/18/2016.
 */
@Data
public class SearchReopenedClaimDetailDto {
    private String policyHolderName;
    private String claimNumber;
    private String clientId;
    private String salutation;
    private String assuredFirstName;
    private String assuredLastName;
    private String policyNumber;
    private String assuredNRCNumber;
    private String status;
    private LocalDate rejectionDate;
    private boolean showModalWin;
    private String errorMessage;

    public SearchReopenedClaimDetailDto updateWithDetails(GroupHealthCashlessClaim claim) {
        if(isNotEmpty(claim)){
            this.policyHolderName = isNotEmpty(claim.getGhProposer()) ? claim.getGhProposer().getProposerName() : StringUtils.EMPTY;
            this.claimNumber = claim.getGroupHealthCashlessClaimId();
            this.status = claim.getStatus().getDescription();
            this.rejectionDate = claim.getClaimRejectionDate();
            GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail = claim.getGroupHealthCashlessClaimPolicyDetail();
            if(isNotEmpty(groupHealthCashlessClaimPolicyDetail)){
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

    public SearchReopenedClaimDetailDto updateWithShowModalWin() {
        this.showModalWin = Boolean.TRUE;
        return this;
    }

    public SearchReopenedClaimDetailDto updateWithErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
