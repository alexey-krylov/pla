package com.pla.sharedkernel.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Samir on 7/8/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class PolicyNumber {

    private String policyNumber;

    public PolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
}
