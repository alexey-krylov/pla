package com.pla.sharedkernel.domain.model;

import com.pla.sharedkernel.identifier.PolicyId;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * Created by Samir on 8/3/2015.
 */
@ValueObject
@Getter
public class Policy {

    private PolicyId policyId;

    private PolicyNumber policyNumber;

    private String policyHolderName;

    public Policy(PolicyId policyId, PolicyNumber policyNumber, String policyHolderName) {
        this.policyId = policyId;
        this.policyNumber = policyNumber;
        this.policyHolderName = policyHolderName;
    }
}
