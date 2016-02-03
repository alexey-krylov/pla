package com.pla.grouplife.claim.domain.model;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by ak on 7/1/2016.
 */
public enum AssuredGainfulActivities {
    YES("Yes"),NO("No");
    private String description;

    AssuredGainfulActivities(String description){
        this.description = description;
    }
    @Override
    public String toString() {
        return  description;
    }

    public static List<Map<String,Object>> getGainfulActivities(){
        return Arrays.asList(AssuredGainfulActivities.values()).parallelStream().map(new Function<AssuredGainfulActivities,Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(AssuredGainfulActivities assuredGain) {
                Map<String,Object> assuredGainMap = Maps.newLinkedHashMap();
                assuredGainMap.put("assuredConfinedType",assuredGain.name());
                assuredGainMap.put("description",assuredGain.toString());
                return assuredGainMap;
            }
        }).collect(Collectors.toList());
    }
}
