package com.pla.grouplife.claim.domain.model;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by ak
 */
public enum DisabilityNature {
    PERMANENT("Permanent"),TEMPORARY("Temporary");
    private String description;
    DisabilityNature(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
    public static List<Map<String, Object>> getDisabilityNature() {
        return Arrays.asList(DisabilityNature.values()).parallelStream().map(new Function<DisabilityNature,Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(DisabilityNature disabilityNature) {
                Map<String, Object> disabilityNatureMap = Maps.newLinkedHashMap();
                disabilityNatureMap.put("policeReportRegistered", disabilityNature.name());
                disabilityNatureMap.put("description", disabilityNature.toString());
                return disabilityNatureMap;
            }
        }).collect(Collectors.toList());
    }
}



