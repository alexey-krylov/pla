package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.Tax;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 4/1/2015.
 */

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
class ServiceTax {

    private Tax tax;

    private BigDecimal value;

    /*
    * @TODO put check for tax with three digits
    * */
    ServiceTax(Tax tax, BigDecimal value) {
        this.tax = tax;
        this.value = value;
    }

}
