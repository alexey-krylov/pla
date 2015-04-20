package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum ProductLineProcessType {

    PURGE_TIME_PERIOD("Purge Time Period"),
    FIRST_REMAINDER(""),
    NO_OF_REMAINDER("No. of Remainder"),
    GAP("Gap"),
    CLOSURE("Closure"),
    EARLY_DEATH_CRITERIA("Early Death Criteria");

    private String description;
    private String fullDescription;
    ProductLineProcessType(String description) {
        this.description = description;
    }

}
