package com.pla.sharedkernel.domain.model;


import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Mirror on 8/21/2015.
 */
@Getter
public enum ClaimType {

    DEATH("Death"), DISABILITY("Disability"),MATURITY("Maturity"),
    ENCASHMENT("Encashment"),SURRENDER("Surrender"),FUNERAL("Funeral");

    private String description;

    ClaimType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }

    public static List<Map<String,Object>> getAllClaimType(){
        return Arrays.asList(ClaimType.values()).parallelStream().map(new Function<ClaimType, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(ClaimType claimType) {
                Map<String,Object> claimTypeMap = Maps.newLinkedHashMap();
                claimTypeMap.put("claimType",claimType.name());
                claimTypeMap.put("description",claimType.toString());
                return claimTypeMap;
            }
        }).collect(Collectors.toList());
    }
}

