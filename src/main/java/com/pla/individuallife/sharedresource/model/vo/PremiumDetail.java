package com.pla.individuallife.sharedresource.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Admin on 8/6/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PremiumDetail {

    private String planId;

    private String planName;

    private BigDecimal planAnnualPremium;

    private BigDecimal annualPremium;

    private BigDecimal semiannualPremium;

    private BigDecimal quarterlyPremium;

    private BigDecimal monthlyPremium;

    private BigDecimal totalPremium;

    private Set<RiderPremium> riderPremiums;



}
