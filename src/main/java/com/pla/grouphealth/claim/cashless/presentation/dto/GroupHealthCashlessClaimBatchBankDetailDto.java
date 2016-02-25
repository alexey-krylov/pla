package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimHCPBankDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimHCPDetail;
import lombok.Data;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@Data
public class GroupHealthCashlessClaimBatchBankDetailDto {
    private String bankName;
    private String branchNumber;
    private String accountNumber;
    private String creditNoteNumber;
    private String sortCode;
    private String paymentMode;
    private LocalDate creditNoteRaisedOn;
    private LocalDate chequeDate;
    private String chequeNumber;

    public GroupHealthCashlessClaimBatchBankDetailDto updateWithDetails(GroupHealthCashlessClaimHCPBankDetail groupHealthCashlessClaimHCPDetail) {
        if(isNotEmpty(groupHealthCashlessClaimHCPDetail)){
            this.bankName = groupHealthCashlessClaimHCPDetail.getBankName();
            this.branchNumber = groupHealthCashlessClaimHCPDetail.getBankBranchCode();
            this.accountNumber = groupHealthCashlessClaimHCPDetail.getBankAccountNumber();
            this.sortCode = groupHealthCashlessClaimHCPDetail.getBankBranchSortCode();
            this.paymentMode = groupHealthCashlessClaimHCPDetail.getPaymentMode();
            this.creditNoteNumber = groupHealthCashlessClaimHCPDetail.getCreditNoteNumber();
            this.creditNoteRaisedOn = groupHealthCashlessClaimHCPDetail.getCreditNoteRaisedOn();
        }
        return this;
    }
}
