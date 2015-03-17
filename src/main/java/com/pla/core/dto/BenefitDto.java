/*
 * Copyright (c) 3/16/15 2:30 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@Getter
@EqualsAndHashCode
public class BenefitDto {

    private String benefitId;

    private String benefitName;


    public BenefitDto(String benefitId, String benefitName) {
        this.benefitId = benefitId;
        this.benefitName = benefitName;
    }
}
