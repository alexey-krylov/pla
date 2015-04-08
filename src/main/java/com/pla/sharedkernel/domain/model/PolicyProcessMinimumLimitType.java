package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum PolicyProcessMinimumLimitType {
    ANNUAL("Minimum Number of Persons per Policy"),
    SEMI_ANNUAL("Minimum Premium");

    private String description;

      PolicyProcessMinimumLimitType(String description) {
        this.description = description;
    }
}
