package com.pla.publishedlanguage.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Samir on 4/10/2015.
 */
@Getter
public class ComputedPremiumDto {

    private PremiumFrequency premiumFrequency;

    private BigDecimal premium;

    public ComputedPremiumDto(PremiumFrequency premiumFrequency, BigDecimal premium) {
        this.premiumFrequency = premiumFrequency;
        this.premium = premium;
    }
}
