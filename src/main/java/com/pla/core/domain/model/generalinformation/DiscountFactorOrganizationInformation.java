package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
        this.value = value.setScale(4,BigDecimal.ROUND_HALF_UP);
    }
}
