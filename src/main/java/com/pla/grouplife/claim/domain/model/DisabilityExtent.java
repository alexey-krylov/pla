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
public enum DisabilityExtent {

    TOTAL("Total"),PARTIAL("Partial");

    private String description;
    DisabilityExtent(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return description;
    }
    public static List<Map<String, Object>> getDisabilityExtent() {
        return Arrays.asList(DisabilityExtent.values()).parallelStream().map(new Function<DisabilityExtent,Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(DisabilityExtent disabilityExtent) {
                Map<String, Object> disabilityExtentMap = Maps.newLinkedHashMap();
                disabilityExtentMap.put("disabilityExtent", disabilityExtent.name());
                disabilityExtentMap.put("description", disabilityExtent.toString());
                return disabilityExtentMap;
            }
        }).collect(Collectors.toList());
    }
}

