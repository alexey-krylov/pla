package com.pla.grouplife.claim.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 9/22/2015.
 */
@Getter
public enum ClaimStatus {

    INTIMATED("Claim Intimated"),RECEIVED("Claim Received"),CANCELLED("Cancelled"),APPROVING("Approving"),PROCESSING("Processing"),APPROVED("Approved"),REPUDIATED("Repudiated"),
    HOLD("Claim Request on hold"),PAID("Paid");


    private String description;

    ClaimStatus(String description){
        this.description = description;
    }
}
