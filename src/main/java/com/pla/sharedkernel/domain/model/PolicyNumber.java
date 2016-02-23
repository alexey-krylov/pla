package com.pla.sharedkernel.domain.model;

import lombok.*;

/**
 * Created by Samir on 7/8/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "policyNumber")
public class PolicyNumber {

    private String policyNumber;

    public PolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
}
