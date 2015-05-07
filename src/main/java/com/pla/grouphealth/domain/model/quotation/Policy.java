package com.pla.grouphealth.domain.model.quotation;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Karunakar on 4/30/2015.
 */

@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
class Policy {

    private PremiumFrequency premiumFrequency;

    private BigDecimal premium;

    Policy(PremiumFrequency premiumFrequency, BigDecimal premium) {
        checkArgument(premiumFrequency != null);
        checkArgument(premium != null && premium.compareTo(BigDecimal.ZERO) == 1);
        this.premiumFrequency = premiumFrequency;
        this.premium = premium;
    }
}
