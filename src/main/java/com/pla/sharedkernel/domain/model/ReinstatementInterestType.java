package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 16-Dec-15.
 */
@Getter
public enum ReinstatementInterestType {

    SIMPLE("Simple"),COMPOUND("Compound");

    private String description;

    ReinstatementInterestType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
