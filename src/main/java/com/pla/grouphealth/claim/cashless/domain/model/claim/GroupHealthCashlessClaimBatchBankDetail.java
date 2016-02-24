package com.pla.grouphealth.claim.cashless.domain.model.claim;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class GroupHealthCashlessClaimBatchBankDetail {
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
