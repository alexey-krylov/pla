package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 4/1/2015.
 */
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = false)
public class DiscountFactorOrganizationInformation {

    private DiscountFactorItem discountFactorItem;

    private BigDecimal value;

    public DiscountFactorOrganizationInformation(DiscountFactorItem discountFactorItem, BigDecimal value) {
        this.discountFactorItem = discountFactorItem;
        this.value = value.setScale(4, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal getAnnualDiscountFactor(Set<DiscountFactorOrganizationInformation> discountFactorOrganizationInformations) {
        DiscountFactorOrganizationInformation annualDiscountFactorItem = findDiscountFactorItem(DiscountFactorItem.ANNUAL, discountFactorOrganizationInformations);
        return annualDiscountFactorItem.getValue();
    }

    public static BigDecimal getQuarterlyDiscountFactor(Set<DiscountFactorOrganizationInformation> discountFactorOrganizationInformations) {
        DiscountFactorOrganizationInformation annualDiscountFactorItem = findDiscountFactorItem(DiscountFactorItem.QUARTERLY, discountFactorOrganizationInformations);
        return annualDiscountFactorItem.getValue();
    }

    public static BigDecimal getSemiAnnualDiscountFactor(Set<DiscountFactorOrganizationInformation> discountFactorOrganizationInformations) {
        DiscountFactorOrganizationInformation annualDiscountFactorItem = findDiscountFactorItem(DiscountFactorItem.SEMI_ANNUAL, discountFactorOrganizationInformations);
        return annualDiscountFactorItem.getValue();
    }

    private static DiscountFactorOrganizationInformation findDiscountFactorItem(DiscountFactorItem discountFactorItem, Set<DiscountFactorOrganizationInformation> discountFactorOrganizationInformations) {
        List<DiscountFactorOrganizationInformation> discountFactorOrganizationInformationList = discountFactorOrganizationInformations.stream().filter(new Predicate<DiscountFactorOrganizationInformation>() {
            @Override
            public boolean test(DiscountFactorOrganizationInformation discountFactorOrganizationInformation) {
                return discountFactorItem.equals(discountFactorOrganizationInformation.discountFactorItem);
            }
        }).collect(Collectors.toList());
        checkArgument(isNotEmpty(discountFactorOrganizationInformationList), discountFactorItem.getDescription() + " discount factor is not found");
        return discountFactorOrganizationInformationList.get(0);
    }
}
