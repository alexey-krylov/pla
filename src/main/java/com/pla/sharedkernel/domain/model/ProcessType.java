package com.pla.sharedkernel.domain.model;

import com.pla.core.domain.model.notification.WaitingForEnum;

import java.util.EnumSet;

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
    QUOTATION("Quotation", EnumSet.of(WaitingForEnum.QUOTATION_RESPONSE));

    public String description;
    private EnumSet<WaitingForEnum> waitingForList;

    ProcessType(String description) {
        this.description = description;
    }


    ProcessType(String description, EnumSet<WaitingForEnum> waitingForList) {
        this.description = description;
        this.waitingForList = waitingForList;
    }

    public EnumSet<WaitingForEnum> getWaitingForList() {
        return waitingForList;
    }

    public String toString() {
        return description;
    }

}
