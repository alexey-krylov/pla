package com.pla.sharedkernel.domain.model;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
public enum PlanType {
    INVESTMENT("Investment"), NON_INVESTMENT("Non-Investment");

    private String description;

    PlanType(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
