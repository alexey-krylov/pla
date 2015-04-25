package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum PolicyProcessMinimumLimitType {
    MINIMUM_NUMBER_OF_PERSON_PER_POLICY("Minimum Number of Persons per Policy"),
    MINIMUM_PREMIUM("Minimum Premium");

    private String description;
    private String fullDescription;

    PolicyProcessMinimumLimitType(String description) {
        this.description = description;
    }

}
