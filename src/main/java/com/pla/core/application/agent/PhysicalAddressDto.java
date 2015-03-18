/*
 * Copyright (c) 3/16/15 7:46 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.agent;

import com.pla.core.domain.model.agent.GeoDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.NotEmpty;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.*;
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
public class PhysicalAddressDto {

    @NotNull(message = "{Physical address1 cannot be null}")
    @NotEmpty(message = "{Physical address1 cannot be empty}")
    private String physicalAddressLine1;

    private String physicalAddressLine2;

    private GeoDetailDto physicalGeoDetail = new GeoDetailDto();

}
