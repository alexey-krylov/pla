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
import java.util.function.Predicate;
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

    GeoDto(String provinceId, String provinceName, List<Map<String, Object>> cities) {
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.cities = cities;
    }

    public static List<GeoDto> transformToGeoDto(List<Map<String, Object>> provinces, List<Map<String, Object>> cities) {
        List<GeoDto> geoDtoList = new ArrayList<>();
        geoDtoList = provinces.stream().map(new CreateGeoDtoFromGeoDetail(cities)).collect(Collectors.toList());
        return geoDtoList;
    }

    private static class CreateGeoDtoFromGeoDetail implements Function<Map<String, Object>, GeoDto> {

        List<Map<String, Object>> cities;

        CreateGeoDtoFromGeoDetail(List<Map<String, Object>> cities) {
            this.cities = cities;
        }

        @Override
        public GeoDto apply(Map<String, Object> stringObjectMap) {
            String provinceId = (String) stringObjectMap.get("geoId");
            String provinceName = (String) stringObjectMap.get("geoName");
            List<Map<String, Object>> citiesByProvince = cities.stream().filter(new FilterCityByProvince(provinceId)).collect(Collectors.toList());
            return new GeoDto(provinceId, provinceName, citiesByProvince);
        }

    }

    private static class FilterCityByProvince implements Predicate<Map<String, Object>> {

        String provinceId;

        FilterCityByProvince(String provinceId) {
            this.provinceId = provinceId;
        }

        @Override
        public boolean test(Map<String, Object> stringObjectMap) {
            return provinceId.equals((String) stringObjectMap.get("parentGeoId"));
        }
    }

}
