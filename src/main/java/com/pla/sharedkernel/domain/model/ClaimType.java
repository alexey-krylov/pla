package com.pla.sharedkernel.domain.model;


import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Mirror on 8/21/2015.
 */
public enum ClaimType {
    DEATH("Death"), DISABILITY("Disability");

    private String description;

    ClaimType(String description) {
        this.description = description;
    }

    public static List<Map<String, String>> getAllClaimTypes() {
       return Arrays.asList(ClaimType.values()).stream().map(new Function<ClaimType, Map<String, String>>() {
            @Override
            public Map<String, String> apply(ClaimType claimType) {
                Map<String, String> claimTypeMap = Maps.newHashMap();
                claimTypeMap.put("code", claimType.name());
                claimTypeMap.put("description", claimType.description);
                return claimTypeMap;
            }

        }).collect(Collectors.toList());
    }
}

