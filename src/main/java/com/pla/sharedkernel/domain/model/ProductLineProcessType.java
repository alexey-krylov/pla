package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum ProductLineProcessType {

    PURGE_TIME_PERIOD("Purge Time Period (Days)"),
    FIRST_REMAINDER("First Reminder (Days)"),
    SECOND_REMAINDER("Second Reminder (Days)"),
    LAPSE("Lapse (Days)"),
    CLOSURE("Closure (Days)"),
    EARLY_DEATH_CRITERIA("Early Death Criteria"),
    TYPE("Type"),
    INTEREST("Interest"),
    CHARGES("Charges");

    private String description;
    private String fullDescription;
    ProductLineProcessType(String description) {
        this.description = description;
    }
}
