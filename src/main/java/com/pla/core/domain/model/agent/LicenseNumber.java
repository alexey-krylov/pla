/*
 * Copyright (c) 3/13/15 8:29 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@ValueObject
@Immutable
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
@EqualsAndHashCode(of = "licenseNumber")
public class LicenseNumber {

    private String licenseNumber;

    public LicenseNumber(String licenseNumber) {
        checkArgument(isNotEmpty(licenseNumber));
        this.licenseNumber = licenseNumber;
    }
}
