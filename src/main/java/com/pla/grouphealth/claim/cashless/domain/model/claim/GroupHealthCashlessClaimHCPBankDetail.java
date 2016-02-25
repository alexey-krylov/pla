package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.core.hcp.domain.model.HCPBankDetail;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.Embeddable;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/25/2016.
 */
@ValueObject
@Immutable
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GroupHealthCashlessClaimHCPBankDetail {
    private String bankName;
    private String bankBranchCode;
    private String bankAccountType;
    private String bankAccountNumber;
    private String bankBranchSortCode;
    private String creditNoteNumber;
    private LocalDate creditNoteRaisedOn;
    private String paymentMode;
    private LocalDate chequeDate;
    private String chequeNumber;

    public GroupHealthCashlessClaimHCPBankDetail updateWithDetails(HCPBankDetail hcpBankDetail) {
        if(isNotEmpty(hcpBankDetail)){
            this.bankName = hcpBankDetail.getBankName();
            this.bankBranchCode = hcpBankDetail.getBankBranchCode();
            this.bankAccountType = hcpBankDetail.getBankAccountType();
            this.bankAccountNumber = hcpBankDetail.getBankAccountNumber();
            this.bankBranchSortCode = hcpBankDetail.getBankBranchSortCode();
        }
        return this;
    }
}
