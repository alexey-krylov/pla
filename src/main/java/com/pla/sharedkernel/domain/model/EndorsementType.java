package com.pla.sharedkernel.domain.model;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
public enum EndorsementType {

    NAME("Correction of Name"),
    ADDRESS("Change of Address"),
    BENEFICIARY("Change/Add Beneficiary"),
    PAYMENT("Change method of Payment"),
    AGENT("Change Agent"),
    CHANGE_PAYER("Change Payer"),
    SUM_ASSURED("Change Sum Assured"),
    DATE_OF_BIRTH("Change Life Assured Date of Birth"),
    MEMBER_ADDITION("Member Addition"),
    MEMBER_DELETION("Member Deletion"),
    PROMOTION("Promotion"),
    NEW_COVER("Introduction of New Cover");

    private String description;

    EndorsementType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}