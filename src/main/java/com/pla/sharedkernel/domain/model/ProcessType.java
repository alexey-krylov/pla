package com.pla.sharedkernel.domain.model;

/**
 * Created by Admin on 3/27/2015.
 */

public enum ProcessType {

    ASSIGNMENT("Assignment"),
    CLAIM("Claim"),
    ENDORSEMENT("Endorsement"),
    ENROLLMENT("Enrollment"),
    MATURITY("Maturity Claim"),
    PROPOSAL("Proposal"),
    REINSTATEMENT("Reinstatement"),
    SURRENDER("Surrender Claim"),
    QUOTATION("Quotation");

    public String description;

    ProcessType(String description) {
        this.description = description;
    }

}