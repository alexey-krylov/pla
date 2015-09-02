package com.pla.individuallife.proposal.presentation.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Karunakar on 7/3/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class PremiumDetailDto {

    private String planName;

    private BigDecimal planAnnualPremium;

    private BigDecimal annualPremium;

    private BigDecimal semiannualPremium;

    private BigDecimal quarterlyPremium;

    private BigDecimal monthlyPremium;

    private BigDecimal totalPremium;

    private Set<RiderPremiumDto> riderPremiums;
}
