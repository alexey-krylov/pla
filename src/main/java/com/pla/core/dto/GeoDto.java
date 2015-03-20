/*
 * Copyright (c) 3/20/15 9:22 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: Samir
 * @since 1.0 20/03/2015
 */
@Getter
public class GeoDto {

    private String provinceId;

    private String provinceName;

    List<Map<String, Object>> cities;

    public static List<GeoDto> transformToGeoDto(List<Map<String, Object>> provinces, List<Map<String, Object>> cities) {
        List<GeoDto> geoDtoList = new ArrayList<>();
        geoDtoList = provinces.stream().map(new Function<Map<String, Object>, GeoDto>() {
            @Override
            public GeoDto apply(Map<String, Object> stringObjectMap) {
                return null;
            }
        }).collect(Collectors.toList());

        return geoDtoList;
    }

}
