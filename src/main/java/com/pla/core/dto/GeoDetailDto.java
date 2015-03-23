/*
 * Copyright (c) 3/16/15 7:45 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@Getter
@Setter
@NoArgsConstructor
public class GeoDetailDto {

    @NotNull(message = "{Postal code cannot be null}")
    private Integer postalCode;

    @NotNull(message = "{Province cannot be null}")
    @NotEmpty(message = "{Province cannot be empty}")
    private String province;

    @NotNull(message = "{City cannot be null}")
    @NotEmpty(message = "{City cannot be empty}")
    private String city;

}
