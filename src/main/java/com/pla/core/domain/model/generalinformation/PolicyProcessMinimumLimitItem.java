package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.PolicyProcessMinimumLimitType;
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
public class PolicyProcessMinimumLimitItem {

    private PolicyProcessMinimumLimitType policyProcessMinimumLimitType;
    private int noOfPersonPerPolicy;
    private int minimumPremium;

    public PolicyProcessMinimumLimitItem(PolicyProcessMinimumLimitType policyProcessMinimumLimitType,int noOfPersonPerPolicy,int minimumPremium) {
        this.policyProcessMinimumLimitType = policyProcessMinimumLimitType;
        this.noOfPersonPerPolicy = noOfPersonPerPolicy;
        this.minimumPremium = minimumPremium;
    }
}
