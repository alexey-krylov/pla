package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.PolicyFeeProcessType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
public class PolicyFeeProcessItem {

    private PolicyFeeProcessType policyFeeProcessType;

    private int  policyFee;

    public PolicyFeeProcessItem(PolicyFeeProcessType policyFeeProcessType,int policyFee) {
        this.policyFeeProcessType = policyFeeProcessType;
        this.policyFee = policyFee;
    }
}
