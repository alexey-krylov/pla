package com.pla.quotation.domain.model.grouplife;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Samir on 4/7/2015.
 */

@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
public class Policy {

    private PremiumFrequency premiumFrequency;

    private BigDecimal premium;

    public Policy(PremiumFrequency premiumFrequency, BigDecimal premium) {
        checkArgument(premiumFrequency != null);
        checkArgument(premium != null && premium.compareTo(BigDecimal.ZERO) == 1);
        this.premiumFrequency = premiumFrequency;
        this.premium = premium;
    }
}
