package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 4/27/2015.
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
public class DiscountFactorProcessInformation {

    private Set<DiscountFactorOrganizationInformation> discountFactorItems;


    public DiscountFactorProcessInformation(Set<DiscountFactorOrganizationInformation> discountFactorItems) {
        this.discountFactorItems = discountFactorItems;
    }

    public static DiscountFactorProcessInformation create(List<Map<DiscountFactorItem, BigDecimal>> listOfDiscountFactorItem) {
        Set<DiscountFactorOrganizationInformation> discountFactorItem = listOfDiscountFactorItem.stream().map(new DiscountFactorInformationTransformer()).collect(Collectors.toSet());
        return new DiscountFactorProcessInformation(discountFactorItem);
    }

    private static class DiscountFactorInformationTransformer implements Function<Map<DiscountFactorItem, BigDecimal>, DiscountFactorOrganizationInformation> {
        @Override
        public DiscountFactorOrganizationInformation apply(Map<DiscountFactorItem, BigDecimal> discountFactorItemMap) {
            Map.Entry<DiscountFactorItem, BigDecimal> discountFactorItem = discountFactorItemMap.entrySet().iterator().next();
            DiscountFactorOrganizationInformation discountFactorOrganizationInformation = new DiscountFactorOrganizationInformation(discountFactorItem.getKey(), discountFactorItem.getValue());
            return discountFactorOrganizationInformation;
        }
    }
}
