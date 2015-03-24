/*
 * Copyright (c) 3/14/15 12:01 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import com.pla.sharedkernel.domain.model.OverrideCommissionApplicable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 14/03/2015
 */
@ValueObject
@Getter(value = AccessLevel.PACKAGE)
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Designation {

    private static final String BDE_CODE = "BDE";

    private String designationCode;

    private String designationName;

    Designation(String designationCode, String designationName) {
        checkArgument(isNotEmpty(designationCode));
        checkArgument(isNotEmpty(designationName));
        this.designationCode = designationCode;
        this.designationName = designationName;
    }

    public OverrideCommissionApplicable getOverrideCommissionApplicable() {
        if (BDE_CODE.equalsIgnoreCase(this.designationCode)) {
            return OverrideCommissionApplicable.YES;
        }
        return OverrideCommissionApplicable.NO;
    }
}
