package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Samir on 8/3/2015.
 */
@Getter
public class EndorsementNumber {

    private String endorsementNumber;

    public EndorsementNumber(String endorsementNumber) {
        this.endorsementNumber = endorsementNumber;
    }
}
