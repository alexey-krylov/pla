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
public enum AssuredConfinedToHouse {
    YES("Yes"),NO("No");
    private String description;

    AssuredConfinedToHouse(String description){
        this.description = description;
    }
    @Override
    public String toString() {
        return  description;
    }

    public static List<Map<String,Object>> getAssuredConfinedTypes(){
        return Arrays.asList(AssuredConfinedToHouse.values()).parallelStream().map(new Function<AssuredConfinedToHouse,Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(AssuredConfinedToHouse assuredTask) {
                Map<String,Object> assuredConfinedMap = Maps.newLinkedHashMap();
                assuredConfinedMap.put("assuredConfinedType",assuredTask.name());
                assuredConfinedMap.put("description",assuredTask.toString());
                return assuredConfinedMap;
            }
        }).collect(Collectors.toList());
    }
}