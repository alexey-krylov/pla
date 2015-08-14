package com.pla.publishedlanguage.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Samir on 5/22/2015.
 */
@Getter
@Setter
@AllArgsConstructor
public class BasicPremiumDto {

    private PremiumFrequency premiumFrequency;

    private BigDecimal basicPremium;
}
