/*
 * Copyright (c) 3/13/15 9:20 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@ValueObject
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class GeoDetail {

    private int postalCode;

    private String province;

    private String city;

    GeoDetail(int postalCode, String province, String city) {
        checkArgument(isNotEmpty(province));
        checkArgument(isNotEmpty(city));
        this.postalCode = postalCode;
        this.province = province;
        this.city = city;
    }
}
