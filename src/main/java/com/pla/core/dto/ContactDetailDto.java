/*
 * Copyright (c) 3/16/15 7:44 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

import java.util.Map;

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

    public static ContactDetailDto transformToContactDetailDto(Map<String, Object> agentDetail) {
        ContactDetailDto contactDetailDto = new ContactDetailDto();
        contactDetailDto.setHomePhoneNumber(agentDetail.get("homePhoneNumber") != null ? (String) agentDetail.get("homePhoneNumber") : null);
        contactDetailDto.setMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : null);
        contactDetailDto.setWorkPhoneNumber(agentDetail.get("workPhoneNumber") != null ? (String) agentDetail.get("workPhoneNumber") : null);
        contactDetailDto.setEmailAddress(agentDetail.get("email") != null ? (String) agentDetail.get("email") : null);
        contactDetailDto.setAddressLine1(agentDetail.get("addressLine1") != null ? (String) agentDetail.get("addressLine1") : null);
        contactDetailDto.setAddressLine2(agentDetail.get("addressLine2") != null ? (String) agentDetail.get("addressLine2") : null);
        contactDetailDto.setGeoDetail(GeoDetailDto.transformToGeoDetailDtoPrimaryContactDetail(agentDetail));
        return contactDetailDto;
    }

}
