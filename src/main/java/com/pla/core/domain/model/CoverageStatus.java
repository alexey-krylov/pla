/*
 * Copyright (c) 3/10/15 9:04 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
public enum CoverageStatus {

    ACTIVE("Active"), INACTIVE("Inactive"), INUSE("In Use");

    private String description;

    CoverageStatus(String description) {
        this.description = description;

    }

}
