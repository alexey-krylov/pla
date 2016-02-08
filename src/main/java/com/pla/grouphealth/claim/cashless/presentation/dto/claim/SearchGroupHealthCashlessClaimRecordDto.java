package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Rudra on 1/6/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchGroupHealthCashlessClaimRecordDto {
    private String batchNumber;
    private String policyNumber;
    private String policyHolderName;
    private String clientId;
    private String hcpCode;
    private String groupHealthCashlessClaimId;
    private String underwriterLevel;

    public SearchGroupHealthCashlessClaimRecordDto updateWithUnderwriterLevel(String level) {
        this.underwriterLevel = level;
        return this;
    }
}
