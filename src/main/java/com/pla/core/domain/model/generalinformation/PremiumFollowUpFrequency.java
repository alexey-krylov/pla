package com.pla.core.domain.model.generalinformation;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import com.pla.sharedkernel.exception.ProcessInfoException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static com.pla.sharedkernel.exception.ProcessInfoException.raiseProductLineItemNotFoundException;

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

    public int getTheProductLineProcessTypeValue(ProductLineProcessType productLineProcessType) throws ProcessInfoException {
        Optional<ProductLineProcessItem> optionalProductLineProcessItem = premiumFollowUpFrequencyItems.stream().filter(new Predicate<ProductLineProcessItem>() {
            @Override
            public boolean test(ProductLineProcessItem productLineProcessItem) {
                if (productLineProcessType.equals(productLineProcessItem.getProductLineProcessItem())){
                    return true;
                }
                return false;
            }
        }).findAny();
        if (!optionalProductLineProcessItem.isPresent()) {
            raiseProductLineItemNotFoundException();
        }
        return optionalProductLineProcessItem.get().getValue();
    }
}
