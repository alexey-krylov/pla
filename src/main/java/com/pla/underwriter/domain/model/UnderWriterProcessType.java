package com.pla.underwriter.domain.model;

/**
 * Created by Admin on 5/8/2015.
 */
public enum UnderWriterProcessType {

    ENROLLMENT("Enrollment"),CLAIM("Claim");

    private String description;

    UnderWriterProcessType(String description) {
        this.description = description;
    }
}
