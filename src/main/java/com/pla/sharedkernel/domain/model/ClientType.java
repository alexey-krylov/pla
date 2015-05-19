package com.pla.sharedkernel.domain.model;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
public enum ClientType {

    GROUP("Group"), INDIVIDUAL("Individual");

    private String description;

    ClientType(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
