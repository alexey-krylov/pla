package com.pla.core.domain.model;

/**
 * Created by Admin on 3/27/2015.
 */
public enum ProcessType {

    PROPOSAL("Proposal"),
    CLAIM("Claim"),
    REINSTATEMENT("Reinstatement"),
    ENDORSEMENT("Endorsement"),
    SURRENDER("Surrender"),
    MATURITY("Maturity");

    private String description;

    ProcessType(String description) {
        this.description = description;
    }
}
