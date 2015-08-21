package com.pla.sharedkernel.domain.model;

import lombok.Getter;

import java.util.EnumSet;

/**
 * Created by Admin on 3/27/2015.
 */
@Getter
public enum ProcessType {

    ASSIGNMENT("Assignment") {
        @Override
        public boolean isValidWaitingFor(WaitingForEnum waitingForEnum) {
            return false;
        }
    },
    CLAIM("Claim",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS)) {
        @Override
        public boolean isValidWaitingFor(WaitingForEnum waitingForEnum) {
            return getWaitingForList().contains(waitingForEnum);
        }
    },
    ENDORSEMENT("Endorsement",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS,WaitingForEnum.PREMIUM)) {
        @Override
        public boolean isValidWaitingFor(WaitingForEnum waitingForEnum) {
            return getWaitingForList().contains(waitingForEnum);
        }
    },
    ENROLLMENT("Enrollment") {
        @Override
        public boolean isValidWaitingFor(WaitingForEnum waitingForEnum) {
            return getWaitingForList().contains(waitingForEnum);
        }
    },
    MATURITY("Maturity Claim",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS)) {
        @Override
        public boolean isValidWaitingFor(WaitingForEnum waitingForEnum) {
            return getWaitingForList().contains(waitingForEnum);
        }
    },
    PROPOSAL("Proposal",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS,WaitingForEnum.CONSENT_LETTER,WaitingForEnum.MEDICALS,WaitingForEnum.INITIAL_PREMIUM)) {
        @Override
        public boolean isValidWaitingFor(WaitingForEnum waitingForEnum) {
            return getWaitingForList().contains(waitingForEnum);
        }
    },
    REINSTATEMENT("Reinstatement",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS,WaitingForEnum.PREMIUM)) {
        @Override
        public boolean isValidWaitingFor(WaitingForEnum waitingForEnum) {
            return getWaitingForList().contains(waitingForEnum);
        }
    },
    SURRENDER("Surrender Claim",EnumSet.of(WaitingForEnum.MANDATORY_DOCUMENTS)) {
        @Override
        public boolean isValidWaitingFor(WaitingForEnum waitingForEnum) {
            return getWaitingForList().contains(waitingForEnum);
        }
    },
    QUOTATION("Quotation", EnumSet.of(WaitingForEnum.QUOTATION_RESPONSE)) {
        @Override
        public boolean isValidWaitingFor(WaitingForEnum waitingForEnum) {
            return getWaitingForList().contains(waitingForEnum);
        }
    };

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
    public abstract boolean isValidWaitingFor(WaitingForEnum waitingForEnum);
}
