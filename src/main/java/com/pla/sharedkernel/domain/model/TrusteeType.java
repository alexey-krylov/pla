package com.pla.sharedkernel.domain.model;

/**
 * Created by Admin on 9/14/2015.
 */
public enum TrusteeType {
    INDIVIDUAL("Individual"),ORGANIZATION("Organization");

    private String description;

    TrusteeType(String description){
        this.description  = description;
    }
}
