package com.pla.sharedkernel.domain.model;

/**
 * Created by User on 3/31/2015.
 */
public enum CommissionType {

    OVERRIDE("Override"), NORMAL("Normal");

    private String description;

    CommissionType(String description) {
        this.description = description;
    }

}
