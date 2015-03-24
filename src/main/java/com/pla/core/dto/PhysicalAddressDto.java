/*
 * Copyright (c) 3/16/15 7:46 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Map;

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


    public static PhysicalAddressDto transformToPhysicalAddressDto(Map<String, Object> agentDetail) {
        PhysicalAddressDto physicalAddressDto = new PhysicalAddressDto();
        physicalAddressDto.setPhysicalAddressLine1(agentDetail.get("physicalAddressLine1") != null ? (String) agentDetail.get("physicalAddressLine1") : null);
        physicalAddressDto.setPhysicalAddressLine2(agentDetail.get("physicalAddressLine2") != null ? (String) agentDetail.get("physicalAddressLine2") : null);
        physicalAddressDto.setPhysicalGeoDetail(GeoDetailDto.transformToGeoDetailDtoForPhysicalAddress(agentDetail));
        return physicalAddressDto;
    }
}
