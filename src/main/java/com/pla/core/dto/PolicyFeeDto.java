package com.pla.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 04-Jan-16.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyFeeDto {
    private BigDecimal annualPolicyFee;
    private BigDecimal semiAnnualPolicyFee;
    private BigDecimal quarterlyPolicyFee;
    private BigDecimal monthlyPolicyFee;
}
