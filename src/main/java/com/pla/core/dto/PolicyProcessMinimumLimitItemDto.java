package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.PolicyProcessMinimumLimitType;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 4/7/2015.
 */
@Getter
@Setter
public class PolicyProcessMinimumLimitItemDto {
    private PolicyProcessMinimumLimitType policyProcessMinimumLimitType;
    private int noOfPersonPerPolicy;
    private int minimumPremium;
}
