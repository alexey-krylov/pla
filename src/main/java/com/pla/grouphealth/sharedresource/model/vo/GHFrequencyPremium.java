package com.pla.grouphealth.sharedresource.model.vo;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by Samir on 4/7/2015.
 */

@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class GHFrequencyPremium {

    private PremiumFrequency premiumFrequency;

    @Setter
    private BigDecimal premium;

    public GHFrequencyPremium(PremiumFrequency premiumFrequency, BigDecimal premium) {
        this.premiumFrequency = premiumFrequency;
        this.premium = premium;
    }
}
