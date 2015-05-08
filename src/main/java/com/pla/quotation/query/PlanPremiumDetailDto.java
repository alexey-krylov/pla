package com.pla.quotation.query;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Samir on 4/29/2015.
 */
@Getter
@Setter
public class PlanPremiumDetailDto {

    private String planId;

    private String planCode;

    private BigDecimal premiumAmount;
}
