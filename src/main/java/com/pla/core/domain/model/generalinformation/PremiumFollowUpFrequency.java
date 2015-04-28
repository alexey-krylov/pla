package com.pla.core.domain.model.generalinformation;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Created by Admin on 4/27/2015.
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
public class PremiumFollowUpFrequency {

    private PremiumFrequency premiumFrequency;

    private Set<ProductLineProcessItem> premiumFollowUpFrequencyItems;

    public PremiumFollowUpFrequency(PremiumFrequency premiumFrequency,Set<ProductLineProcessItem> premiumFollowUpFrequencyItems) {
        this.premiumFrequency = premiumFrequency;
        this.premiumFollowUpFrequencyItems = premiumFollowUpFrequencyItems;
    }
}
