package com.pla.sharedkernel.domain.model;

/**
 * Created by Samir on 4/5/2015.
 */
public enum PremiumFactor {

    FLAT_AMOUNT("Flat Amount"), PER_THOUSAND("Per Thousand");

    private String description;

    PremiumFactor(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
