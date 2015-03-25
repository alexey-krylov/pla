/*
 * Copyright (c) 3/16/15 7:45 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import lombok.AllArgsConstructor;
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
public class GeoDetailDto {

    @NotNull(message = "{Postal code cannot be null}")
    private Integer postalCode;

    @NotNull(message = "{Province cannot be null}")
    @NotEmpty(message = "{Province cannot be empty}")
    private String provinceName;

    @NotNull(message = "{City cannot be null}")
    @NotEmpty(message = "{City cannot be empty}")
    private String cityName;

    private String provinceCode;

    private String cityCode;


    public GeoDetailDto(Integer postalCode, String cityCode, String provinceCode) {
        this.postalCode = postalCode;
        this.cityCode = cityCode;
        this.provinceCode = provinceCode;
    }

    public static GeoDetailDto transformToGeoDetailDtoPrimaryContactDetail(Map<String, Object> agentDetail) {
        GeoDetailDto geoDetailDto = new GeoDetailDto();
        geoDetailDto.setPostalCode(agentDetail.get("postalCode") != null ? ((Integer) agentDetail.get("postalCode")).intValue() : null);
        geoDetailDto.setCityCode(agentDetail.get("cityCode") != null ? (String) agentDetail.get("cityCode") : null);
        geoDetailDto.setCityName(agentDetail.get("cityName") != null ? (String) agentDetail.get("cityName") : null);
        geoDetailDto.setProvinceCode(agentDetail.get("provinceCode") != null ? (String) agentDetail.get("provinceCode") : null);
        geoDetailDto.setProvinceName(agentDetail.get("provinceName") != null ? (String) agentDetail.get("provinceName") : null);
        return geoDetailDto;

    }

    public static GeoDetailDto transformToGeoDetailDtoForPhysicalAddress(Map<String, Object> agentDetail) {
        GeoDetailDto geoDetailDto = new GeoDetailDto();
        geoDetailDto.setPostalCode(agentDetail.get("physicalAddressPostalCode") != null ? ((Integer) agentDetail.get("physicalAddressPostalCode")).intValue() : null);
        geoDetailDto.setCityCode(agentDetail.get("physicalAddressCityCode") != null ? (String) agentDetail.get("physicalAddressCityCode") : null);
        geoDetailDto.setCityName(agentDetail.get("physicalAddressCityName") != null ? (String) agentDetail.get("physicalAddressCityName") : null);
        geoDetailDto.setProvinceCode(agentDetail.get("physicalAddressProvinceCode") != null ? (String) agentDetail.get("physicalAddressProvinceCode") : null);
        geoDetailDto.setProvinceName(agentDetail.get("physicalAddressProvinceName") != null ? (String) agentDetail.get("physicalAddressProvinceName") : null);
        return geoDetailDto;
    }
}
