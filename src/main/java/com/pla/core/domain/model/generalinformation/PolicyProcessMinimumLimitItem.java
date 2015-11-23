package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.PolicyProcessMinimumLimitType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
public class PolicyProcessMinimumLimitItem {

    private PolicyProcessMinimumLimitType policyProcessMinimumLimitType;
    private int value;

    public PolicyProcessMinimumLimitItem(PolicyProcessMinimumLimitType policyProcessMinimumLimitType,int value) {
        this.policyProcessMinimumLimitType = policyProcessMinimumLimitType;
        this.value = value;
    }
}
