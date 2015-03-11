package com.pla.core.domain.model.plan;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
public enum CoverageType {

    BASE("Base"), OPTIONAL("Optional");

    private String value;

    CoverageType(String value) {
        this.value = value;
    }


}
