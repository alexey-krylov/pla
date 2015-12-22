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

public class SearchClaimIntimationDto {

    private String claimNumber;
    private String policyNumber;
    private String policyHolderName;
    private String policyHolderClientId;
    private String assuredName;
    private String assuredClientId;
    private String assuredNrcNumber;

}