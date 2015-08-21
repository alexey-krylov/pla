package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 5/8/2015.
 */
@Getter
public enum  RoutingLevel {

    UNDERWRITING_LEVEL_ONE("UnderWriting level 1"),UNDERWRITING_LEVEL_TWO("UnderWriting level 2");

    private String description;

    RoutingLevel(String description) {
        this.description = description;
    }

    public RoutingLevel getNext() {
        return this.ordinal() < RoutingLevel.values().length - 1
                ? RoutingLevel.values()[this.ordinal() + 1]
                : null;
    }

}

