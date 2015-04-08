package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum ModalFactorItem {

    SEMI_ANNUAL("Semi-annual Modal Factor"),QUARTERLY("Quarterly Modal Factor"),
    MONTHLY("Monthly Modal Factor");

    private String description;

    ModalFactorItem(String description) {
        this.description = description;
    }

}
