package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum DiscountFactorItem {

    ANNUAL("Annual Discount Factor"),
    SEMI_ANNUAL("Semi - annual Discount Factor"),
    QUARTERLY("Quarterly Discount Factor");

    private String description;

    DiscountFactorItem(String description) {
        this.description = description;
    }

}
