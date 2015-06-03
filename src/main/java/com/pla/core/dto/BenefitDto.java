/*
 * Copyright (c) 3/16/15 2:30 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@Getter
@Setter
@EqualsAndHashCode
public class BenefitDto {

    private String benefitId;

    private String benefitName;

    private String benefitCode;

    public BenefitDto() {

    }

    public BenefitDto(String benefitId, String benefitName,String benefitCode) {
        this.benefitId = benefitId;
        this.benefitName = benefitName;
        this.benefitCode = benefitCode;
    }

    @Override
    public String toString() {
        return "BenefitDto{" +
                "benefitId='" + benefitId + '\'' +
                ", benefitName='" + benefitName + '\'' +
                ", benefitCode='" + benefitCode + '\'' +
                '}';
    }
}
