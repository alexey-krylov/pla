package com.pla.core.domain.model.plan;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
public enum CoverageBenefitDefinition {
    INCIDENCE("Per Incidence"),
    DAY("Per Day"),
    YEAR("Per Year");

    private String value;

    CoverageBenefitDefinition(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
