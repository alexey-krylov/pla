package com.pla.publishedlanguage.domain.model;

import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Samir on 5/22/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class BasicPremiumDto {

    private PremiumFrequency premiumFrequency;

    private BigDecimal basicPremium;

    private BigDecimal semiAnnualPremium;

    private BigDecimal quarterlyPremium;

    private BigDecimal monthlyPremium;

    private LineOfBusinessEnum lineOfBusinessEnum;

    public BasicPremiumDto(PremiumFrequency premiumFrequency,BigDecimal basicPremium,LineOfBusinessEnum lineOfBusinessEnum){
        this.premiumFrequency  =premiumFrequency;
        this.basicPremium = basicPremium;
        this.lineOfBusinessEnum = lineOfBusinessEnum;
    }

    public BasicPremiumDto(PremiumFrequency premiumFrequency,BigDecimal basicPremium,BigDecimal semiAnnualPremium,BigDecimal quarterlyPremium,BigDecimal monthlyPremium,LineOfBusinessEnum lineOfBusinessEnum){
        this.premiumFrequency  =premiumFrequency;
        this.basicPremium = basicPremium;
        this.semiAnnualPremium = semiAnnualPremium;
        this.quarterlyPremium  = quarterlyPremium;
        this.monthlyPremium = monthlyPremium;
        this.lineOfBusinessEnum = lineOfBusinessEnum;
    }

}
