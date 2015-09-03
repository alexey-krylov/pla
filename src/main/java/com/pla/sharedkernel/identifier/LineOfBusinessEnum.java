package com.pla.sharedkernel.identifier;

import com.pla.sharedkernel.domain.model.ClaimType;
import com.pla.sharedkernel.domain.model.ProcessType;
import lombok.Getter;

import java.util.EnumSet;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@Getter
public enum LineOfBusinessEnum {

    GROUP_HEALTH("Group Health", EnumSet.of(ProcessType.QUOTATION, ProcessType.PROPOSAL, ProcessType.ENDORSEMENT, ProcessType.CLAIM),
            EnumSet.of(ClaimType.DEATH)) {
        @Override
        public boolean isValidProcess(ProcessType processType) {
            return getProcessTypeList().contains(processType);
        }
    },
    GROUP_LIFE("Group Life", EnumSet.of(ProcessType.QUOTATION, ProcessType.PROPOSAL, ProcessType.ENDORSEMENT, ProcessType.CLAIM),
            EnumSet.of(ClaimType.DEATH,ClaimType.FUNERAL,ClaimType.DISABILITY)) {
        @Override
        public boolean isValidProcess(ProcessType processType) {
            return getProcessTypeList().contains(processType);
        }
    },
    INDIVIDUAL_LIFE("Individual Life", EnumSet.of(ProcessType.QUOTATION, ProcessType.PROPOSAL, ProcessType.ENDORSEMENT, ProcessType.CLAIM, ProcessType.SURRENDER,
            ProcessType.MATURITY, ProcessType.REINSTATEMENT),EnumSet.of(ClaimType.DEATH,ClaimType.FUNERAL,ClaimType.DISABILITY,
            ClaimType.ENCASHMENT, ClaimType.SURRENDER,ClaimType.MATURITY)) {
        @Override
        public boolean isValidProcess(ProcessType processType) {
            return getProcessTypeList().contains(processType);
        }
    };

    private String description;

    private EnumSet<ProcessType> processTypeList;

    private EnumSet<ClaimType> claimTypes;

    LineOfBusinessEnum(String description) {
        this.description = description;
    }

    LineOfBusinessEnum(String description, EnumSet<ProcessType> processTypeList,EnumSet<ClaimType> claimTypes) {
        this.description = description;
        this.processTypeList = processTypeList;
        this.claimTypes = claimTypes;
    }



    public String toString() {
        return description;
    }

    public abstract boolean isValidProcess(ProcessType processType);
}
