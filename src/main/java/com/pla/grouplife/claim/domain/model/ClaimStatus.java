package com.pla.grouplife.claim.domain.model;

import lombok.Getter;


@Getter
public enum ClaimStatus {

INTIMATION("Claim Intimated"),EVALUATION("Claim Registered"),CANCELLED("Cancelled"),UNDERWRITING("Routed to UnderWriter"),APPROVED("Approved"),REPUDIATED("Rejected"),AWAITING_DISBURSEMENT("Settlement pending"),PAID_DISBURSED("Settled");


    private String description;

    ClaimStatus(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }

}
