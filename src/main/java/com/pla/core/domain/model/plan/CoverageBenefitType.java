package com.pla.core.domain.model.plan;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
public enum CoverageBenefitType {
    AMOUNT("Amount"),
    PRODUCT_LIMIT("Percentage of Product Limit"),
    COVERAGE_LIMIT("Percentage of Coverage Limit");

    private String value;

    CoverageBenefitType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
