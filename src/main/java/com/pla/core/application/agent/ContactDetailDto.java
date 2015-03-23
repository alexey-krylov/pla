/*
 * Copyright (c) 3/16/15 7:44 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.agent;

import com.pla.core.domain.model.agent.GeoDetail;
import com.pla.sharedkernel.domain.model.EmailAddress;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
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
public class ContactDetailDto {

    @NotNull(message = "{Mobile number cannot be null}")
    private String mobileNumber;

    private String homePhoneNumber;

    private String workPhoneNumber;

    @Email(message = "{Not a valid email}")
    private String emailAddress;

    @NotNull(message = "{Address Line1 cannot be null}")
    @NotEmpty(message = "{Address Line1 cannot be empty}")
    private String addressLine1;

    private String addressLine2;

    private GeoDetailDto geoDetail = new GeoDetailDto();

}
