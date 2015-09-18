package com.pla.sharedkernel.domain.model;


import lombok.Getter;

/**
 * Created by Mirror on 8/21/2015.
 */
@Getter
public enum ClaimType {

    DEATH("Death"), DISABILITY("Disability"),MATURITY("Maturity"),
    ENCASHMENT("Encashment"),SURRENDER("Surrender"),FUNERAL("Funeral");

    private String description;

     ClaimType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }
}

