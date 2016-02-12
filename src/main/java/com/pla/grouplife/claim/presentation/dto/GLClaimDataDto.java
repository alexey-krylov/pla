package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
    private String assuredName;
    private String planName;
    private int age;
    private BigDecimal claimAmount;
    private String claimStatus;
    private String policyNumber;


    public GLClaimDataDto withClaimNumberAndClaimId (String claimNumber, String claimId){
        this.claimNumber=claimNumber;
        this.claimId=claimId;
        return this;
    }

}
