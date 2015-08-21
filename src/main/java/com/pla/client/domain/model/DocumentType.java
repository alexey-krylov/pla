package com.pla.client.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 5/28/2015.
 */
@Getter
public enum DocumentType {

    UNDERWRITER("Under Writer"),MANDATORY("Mandatory Document");

    private String description;

    DocumentType(String description) {
        this.description = description;
    }
}
