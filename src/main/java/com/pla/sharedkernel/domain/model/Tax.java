package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum Tax {

    SERVICE_TAX("Service Tax");

    private String description;
    private String fullDescription;

    Tax(String description) {
        this.description = description;
    }

}
