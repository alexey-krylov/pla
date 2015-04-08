package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum PolicyFeeProcessType {
    ANNUAL("Annual"),
    SEMI_ANNUAL("Semi-Annual"),
    QUARTERLY("Quarterly"),
    MONTHLY("Monthly");

    private String description;

    PolicyFeeProcessType(String description) {
        this.description = description;
    }


}
