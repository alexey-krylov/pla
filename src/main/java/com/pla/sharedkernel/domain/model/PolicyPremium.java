package com.pla.sharedkernel.domain.model;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Samir on 7/8/2015.
 */
@Getter
public class PolicyPremium {

    private PremiumFrequency premiumFrequency;

    private BigDecimal premium;

    private Integer installmentNumber;

    private BigDecimal installmentAmount;


    public PolicyPremium(PremiumFrequency premiumFrequency, BigDecimal premium) {
        this.premiumFrequency = premiumFrequency;
        this.premium = premium;
    }

    public PolicyPremium(Integer installmentNumber, BigDecimal installmentAmount) {
        this.installmentNumber = installmentNumber;
        this.installmentAmount = installmentAmount;
    }
}
