package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.Tax;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 4/22/2015.
 */
@Getter
@Setter
public class ServiceTaxDto {
    private Tax tax;
    private BigDecimal value;
}
