package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Samir on 7/8/2015.
 */
@Getter
public class PolicyNumber {

    private String policyNumber;

    public PolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
}
