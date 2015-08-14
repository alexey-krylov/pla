package com.pla.underwriter.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 5/8/2015.
 */
@Getter
public enum UnderWriterProcessType {

    ENROLLMENT("Enrollment"),CLAIM("Claim");

    private String description;

    UnderWriterProcessType(String description) {
        this.description = description;
    }
}
