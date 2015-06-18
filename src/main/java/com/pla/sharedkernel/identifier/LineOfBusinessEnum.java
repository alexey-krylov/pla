package com.pla.sharedkernel.identifier;

import com.pla.sharedkernel.domain.model.ProcessType;
import lombok.Getter;

import java.util.EnumSet;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@Getter
public enum LineOfBusinessEnum {

    GROUP_HEALTH("Group Health",EnumSet.of(ProcessType.QUOTATION,ProcessType.PROPOSAL,ProcessType.ENDORSEMENT,ProcessType.CLAIM)),
    GROUP_LIFE("Group Life",EnumSet.of(ProcessType.QUOTATION,ProcessType.PROPOSAL,ProcessType.ENDORSEMENT,ProcessType.CLAIM)),
    INDIVIDUAL_LIFE("Individual Life",EnumSet.of(ProcessType.QUOTATION,ProcessType.PROPOSAL,ProcessType.ENDORSEMENT,ProcessType.CLAIM,ProcessType.SURRENDER,
            ProcessType.MATURITY,ProcessType.REINSTATEMENT));

    private String description;

    private EnumSet<ProcessType> processTypeList;

    LineOfBusinessEnum(String description) {
        this.description = description;
    }

    LineOfBusinessEnum(String description, EnumSet<ProcessType> processTypeList) {
        this.description = description;
        this.processTypeList = processTypeList;
    }

    public String toString() {
        return description;
    }
}
