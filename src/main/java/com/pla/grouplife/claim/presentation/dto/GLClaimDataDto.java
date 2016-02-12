package com.pla.grouplife.claim.presentation.dto;

import com.pla.sharedkernel.domain.model.ClaimType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by ak
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class GLClaimDataDto {

    private String claimId;
    private String claimNumber;
    private String policyHolderName;
    private String policyHolderClientId;
    private String assuredName;
    private String policyNumber;
    private String assuredNrcNumber;
    private String assuredClientId;
    private ClaimType claimType;
    private String claimStatus;
    private String routingLevel;

    public GLClaimDataDto withClaimNumberAndClaimId (String claimNumber, String claimId){
        this.claimNumber=claimNumber;
        this.claimId=claimId;
        return this;
    }

}
