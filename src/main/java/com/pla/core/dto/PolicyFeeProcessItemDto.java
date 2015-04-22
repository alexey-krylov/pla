package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.PolicyFeeProcessType;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 4/22/2015.
 */
@Getter
@Setter
public class PolicyFeeProcessItemDto {
    private PolicyFeeProcessType policyFeeProcessType;
    private int  policyFee;

    public PolicyFeeProcessItemDto() {
    }
}
