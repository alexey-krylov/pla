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
public enum ClaimIntimationType {

    EARLY("Early Claim"),LATE("Late Claim");

    private String description;

    ClaimIntimationType(String description){
        this.description = description;
    }

    public static List<Map<String,Object>> getAllClaimIntimationTypes(){
        return Arrays.asList(ClaimIntimationType.values()).parallelStream().map(new Function<ClaimIntimationType,  Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(ClaimIntimationType intimationType) {
                Map<String,Object> intimationTypeMap = Maps.newLinkedHashMap();
                intimationTypeMap.put("claimIntimationType",intimationType.name());
                intimationTypeMap.put("description",intimationType.toString());
                return intimationTypeMap;
            }
        }).collect(Collectors.toList());
    }

}
