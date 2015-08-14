package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by User on 3/31/2015.
 */
@Getter
public enum CommissionTermType {


    SINGLE("Single"), RANGE("Range");

    private String description;

    CommissionTermType(String description) {
        this.description = description;
    }

}
