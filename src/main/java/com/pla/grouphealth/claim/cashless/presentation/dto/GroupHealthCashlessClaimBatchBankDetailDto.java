package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Data;
import org.joda.time.LocalDate;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@Data
public class GroupHealthCashlessClaimBatchBankDetailDto {
    private String bankName;
    private String branchNumber;
    private String accountNumber;
    private String chequeNumber;
    private String creditNoteNumber;
    private String sortCode;
    private String paymentMode;
    private LocalDate creditNoteRaisedOn;
    private LocalDate chequeDate;
}
