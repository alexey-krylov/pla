package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 4/22/2015.
 */
@Getter
@Setter
public class DiscountFactorInformationDto {
    private DiscountFactorItem discountFactorItem;
    private BigDecimal value;
}
