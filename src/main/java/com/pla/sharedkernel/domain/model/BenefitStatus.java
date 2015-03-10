/*
 * Copyright (c) 3/10/15 9:26 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.domain.model;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
public enum BenefitStatus {


    ACTIVE("Active"), INACTIVE("Inactive"), INUSE("In Use");

    private String description;

    BenefitStatus(String description) {
        this.description = description;

    }
}
