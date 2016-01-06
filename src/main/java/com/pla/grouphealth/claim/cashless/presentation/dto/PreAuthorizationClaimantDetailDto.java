package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorization;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Mohan Sharma on 1/6/2016.
 */
@Getter
@Setter
public class PreAuthorizationClaimantDetailDto {
    private String preAuthorizationId;
    private ClaimantHCPDetailDto claimantHCPDetailDto;
    private int batchNumber;
    private ClaimantPolicyDetailDto claimantPolicyDetailDto;
    private DateTime preAuthorizationDate;
    public static PreAuthorizationClaimantDetailDto getInstance() {
        return new PreAuthorizationClaimantDetailDto();
    }

    public PreAuthorizationClaimantDetailDto updateWithBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
        return this;
    }

    public PreAuthorizationClaimantDetailDto updateWithPreAuthorizationId(String preAuthorizationId) {
        this.preAuthorizationId = preAuthorizationId;
        return this;
    }

    public PreAuthorizationClaimantDetailDto updateWithPreAuthorizationDate(DateTime batchDate) {
        this.preAuthorizationDate = batchDate;
        return this;
    }

    public PreAuthorizationClaimantDetailDto updateWithClaimantHCPDetailDto(ClaimantHCPDetailDto claimantHCPDetailDto) {
        this.claimantHCPDetailDto = claimantHCPDetailDto;
        return this;
    }

    public PreAuthorizationClaimantDetailDto updateWithClaimantPolicyDetailDto(ClaimantPolicyDetailDto claimantPolicyDetailDto) {
        this.claimantPolicyDetailDto = claimantPolicyDetailDto;
        return this;
    }
}
