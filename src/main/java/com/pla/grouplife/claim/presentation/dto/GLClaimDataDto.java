package com.pla.grouplife.claim.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by ak
 */

@Getter
@Setter
@NoArgsConstructor

public class GLClaimDataDto {

    String claimId;
    String claimNumber;
    String claimStatus;
    String assuredName;
    DateTime modifiedOn;
    String policyNumber;
    String policyHolderName;

    public GLClaimDataDto withClaimNumberAndClaimId (String claimNumber, String claimId){
        this.claimNumber=claimNumber;
        this.claimId=claimId;
        return this;
    }

}
