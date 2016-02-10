package com.pla.grouplife.claim.presentation.dto;

import com.pla.sharedkernel.domain.model.ClaimType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nthdimensioncompany on 5/1/2016.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GLClaimIntimationDto {

    private String  claimId;
    private String policyHolderName;
    private String claimNumber;
    private String policyHolderClientId;
    private String assuredName;
    private String policyNumber;
    private String assuredNrcNumber;
    private String assuredClientId;
    private ClaimType claimType;
    private String claimStatus;
}
