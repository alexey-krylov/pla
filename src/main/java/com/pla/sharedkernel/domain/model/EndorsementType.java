package com.pla.sharedkernel.domain.model;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
public enum EndorsementType {

    IND_CHANGE_NAME("Correction of Name"),
    IND_CHANGE_ADDRESS("Change of Address"),
    IND_CHANGE_BENEFICIARY("Change/Add Beneficiary"),
    IND_CHANGE_PAYMENT_METHOD("Change method of Payment"),
    IND_CHANGE_AGENT("Change Agent"),
    IND_CHANGE_PAYER("Change Payer"),
    IND_CHANGE_SUM_ASSURED("Change Sum Assured"),
    IND_CHANGE_DOB("Change Life Assured Date of Birth"),
    GRP_MEMBER_ADDITION("Member Addition"),
    GRP_MEMBER_DELETION("Member Deletion"),
    GRP_PROMOTION("Promotion"),
    GRP_NEW_COVER("Introduction of New Cover");

    private String description;

    EndorsementType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
