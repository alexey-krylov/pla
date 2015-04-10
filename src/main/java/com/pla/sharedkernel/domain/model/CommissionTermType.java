package com.pla.sharedkernel.domain.model;

/**
 * Created by User on 3/31/2015.
 */
public enum CommissionTermType {


    SINGLE("Single"), RANGE("Range");

    private String description;

    CommissionTermType(String description) {
        this.description = description;
    }

}
