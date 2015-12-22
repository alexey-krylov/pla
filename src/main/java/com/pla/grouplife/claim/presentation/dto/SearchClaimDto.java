package com.pla.grouplife.claim.presentation.dto;

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

public class SearchClaimDto {

    private String policyHolderName;
    private String claimNumber;
    private String assuredClientId;
    private String assuredName;
    private String policyNumber;
    private String assuredNrcNumber;


}
