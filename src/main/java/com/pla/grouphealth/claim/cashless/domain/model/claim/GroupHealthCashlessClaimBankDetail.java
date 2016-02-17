package com.pla.grouphealth.claim.cashless.domain.model.claim;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * Author - Mohan Sharma Created on 2/17/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class GroupHealthCashlessClaimBankDetail {
    private String bankName;
    private String bankBranchCode;
    private String bankAccountType;
    private String bankAccountNumber;
}
