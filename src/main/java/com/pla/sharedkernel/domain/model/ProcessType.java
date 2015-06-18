package com.pla.sharedkernel.domain.model;

import lombok.Getter;

import java.util.EnumSet;

/**
 * Created by Admin on 3/27/2015.
 */
@Getter
public enum ProcessType {

    ASSIGNMENT("Assignment"),
    CLAIM("Claim",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS)),
    ENDORSEMENT("Endorsement",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS,WaitingForEnum.PREMIUM)),
    ENROLLMENT("Enrollment"),
    MATURITY("Maturity Claim",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS)),
    PROPOSAL("Proposal",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS,WaitingForEnum.CONSENT_LETTER,WaitingForEnum.MEDICALS,WaitingForEnum.INITIAL_PREMIUM)),
    REINSTATEMENT("Reinstatement",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS,WaitingForEnum.PREMIUM)),
    SURRENDER("Surrender Claim",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS)),
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

    public String toString() {
        return description;
    }

}
