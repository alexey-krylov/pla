package com.pla.individuallife.proposal.domain.model;

/**
 * Created by Karunakar on 6/25/2015.
 */
public enum ILProposalStatus {
    DRAFT("Draft"), PENDING_ACCEPTANCE("Pending Acceptance"), PENDING_DECISION("Pending Decision"), DECLINED ("Declined"), APPROVED("Approved"), RETURNED("Returned"), PENDING_FIRST_PREMIUM("Pending First Premium"), IN_FORCE("In Force"),
    UNDERWRITING_LEVEL_ONE("Pending Acceptance"), UNDERWRITING_LEVEL_TWO("Pending Acceptance");

    private String description;

    ILProposalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
