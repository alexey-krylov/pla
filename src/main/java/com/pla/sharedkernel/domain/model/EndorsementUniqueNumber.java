package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Hemant Neel on 27-Oct-15.
 */
@Getter
public class EndorsementUniqueNumber {
    private String endorsementUniqueNumber;

    public EndorsementUniqueNumber(String endorsementUniqueNumber) {
        this.endorsementUniqueNumber = endorsementUniqueNumber;
    }
}
