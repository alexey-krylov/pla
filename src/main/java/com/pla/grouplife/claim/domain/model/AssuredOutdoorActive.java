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
public enum AssuredOutdoorActive {
    YES("Yes"),NO("No");
    private String description;

    AssuredOutdoorActive(String description){
        this.description = description;
    }
    @Override
    public String toString() {
        return  description;
    }

    public static List<Map<String,Object>> getAssuredOutdoorActivity(){
        return Arrays.asList(AssuredOutdoorActive.values()).parallelStream().map(new Function<AssuredOutdoorActive,Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(AssuredOutdoorActive assuredTask) {
                Map<String,Object> assuredTaskMap = Maps.newLinkedHashMap();
                assuredTaskMap.put("assuredOutdoorType",assuredTask.name());
                assuredTaskMap.put("description",assuredTask.toString());
                return assuredTaskMap;
            }
        }).collect(Collectors.toList());
    }
}
