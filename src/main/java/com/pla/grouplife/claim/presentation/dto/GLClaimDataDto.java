package com.pla.grouplife.claim.presentation.dto;

import com.pla.sharedkernel.domain.model.ClaimType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

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
    private String policyHolderName;
    private String policyHolderClientId;
    private String title;
    private String firstName;
    private String surName;
    private String policyNumber;
    private String assuredNrcNumber;
    private String assuredClientId;
    private ClaimType claimType;
    private String claimStatus;
    private String routingLevel;
    private BigDecimal approvedAmount;
    private DateTime approvedOn;

    public GLClaimDataDto withClaimNumberAndClaimId (String claimNumber, String claimId){
        this.claimNumber=claimNumber;
        this.claimId=claimId;
        return this;
    }

}
