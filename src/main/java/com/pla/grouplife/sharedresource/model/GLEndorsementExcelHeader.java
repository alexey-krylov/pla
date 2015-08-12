package com.pla.grouplife.sharedresource.model;

/**
 * Created by Samir on 8/5/2015.
 */
public enum GLEndorsementExcelHeader {

    PROPOSER_NAME("Proposer Name"), MAN_NUMBER("MAN Number"), NRC_NUMBER("NRC Number"), ANNUAL_INCOME("Annual Income"),
    SALUTATION("Salutation"), FIRST_NAME("First Name"), LAST_NAME("Last Name"), DATE_OF_BIRTH("Date of Birth"),
    GENDER("Gender"), OCCUPATION("Occupation"), CATEGORY("Category"), RELATIONSHIP("Relationship"),
    NO_OF_ASSURED("No Of Assured"), PLAN("Plan"), INCOME_MULTIPLIER("Income Multiplier"), SUM_ASSURED("Sum Assured"),
    PLAN_PREMIUM("Plan Premium"), OLD_CATEGORY("Old Category"), NEW_CATEGORY("New Category"), OLD_ANNUAL_INCOME("Old Annual Income"), NEW_ANNUAL_INCOME("New Annual Income"),
    CLIENT_ID("Client ID"),MAIN_ASSURED_CLIENT_ID("Main Assured Client ID");

    private String description;

    GLEndorsementExcelHeader(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
