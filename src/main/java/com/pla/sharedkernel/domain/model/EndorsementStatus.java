package com.pla.sharedkernel.domain.model;

/**
 * Created by Samir on 8/3/2015.
 */
public enum EndorsementStatus {

    DRAFT("Draft"), APPROVER_PENDING_ACCEPTANCE("Pending Acceptance"), UNDERWRITER_LEVEL1_PENDING_ACCEPTANCE("Pending Acceptance"),
    UNDERWRITER_LEVEL2_PENDING_ACCEPTANCE("Pending Acceptance"), APPROVED("Approved"), REJECTED("Rejected"), CANCELLED("Cancelled"),
    PAYMENT_PENDING("Pending Endorsement Payment"), PAYMENT_RECEIVED("Accepted"), REFUND_PENDING("Accepted"), REFUND_PROCESSED("Accepted"), HOLD("Pending Endorsement Decision"),RETURN("Returned");

    private String description;

    EndorsementStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
