package com.pla.sharedkernel.identifier;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
public enum LineOfBusinessEnum {

    GROUP_HEALTH("Group Health"), GROUP_LIFE("Group Life"), INDIVIDUAL_LIFE("Individual Life");

    private String description;

    LineOfBusinessEnum(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
