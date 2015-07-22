package com.pla.grouphealth.sharedresource.model.vo;

/**
 * Created by Samir on 6/24/2015.
 */
public enum ProposalStatus {

    DRAFT("Draft"), PENDING_ACCEPTANCE("Pending Acceptance"), APPROVED("Approved"), RETURNED("Returned"), PENDING_FIRST_PREMIUM("Pending First Premium"), IN_FORCE("In Force");

    private String description;

    ProposalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
