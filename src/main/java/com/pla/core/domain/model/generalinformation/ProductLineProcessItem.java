package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
 class ProductLineProcessItem {

    private ProductLineProcessType productLineProcessItem;

    private int value;

     ProductLineProcessItem(ProductLineProcessType productLineProcessItem,int value) {
        this.productLineProcessItem = productLineProcessItem;
        this.value = value;
    }
}
