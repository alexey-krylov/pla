package com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Rudra on 1/6/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchPreAuthorizationRecordDto {
    private String batchNumber;
    private String policyNumber;
    private String policyHolderName;
    private String clientId;
    private String hcpCode;
    private String preAuthorizationId;
    private String underwriterLevel;

    public SearchPreAuthorizationRecordDto updateWithUnderwriterLevel(String level) {
        this.underwriterLevel = level;
        return this;
    }
}