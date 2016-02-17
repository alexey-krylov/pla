package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author - Mohan Sharma Created on 2/17/2016.
 */
@NoArgsConstructor
@Setter
@Getter
public class GroupHealthCashlessClaimBankDetailDto {
    private String bankName;
    private String bankBranchCode;
    private String bankAccountType;
    private String bankAccountNumber;
}
