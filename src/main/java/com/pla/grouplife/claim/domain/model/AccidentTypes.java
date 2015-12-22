package com.pla.grouplife.claim.domain.model;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by ak
 */

@Getter
public enum AccidentTypes {
    ANIMAL_OR_INSECT_BITE("Animal or Insect Bite"),MURDER("Murder"),SUICIDE("Suicide"),ROAD_TRAFFIC_ACCIDENT("Road Traffic Accident"),NATURAL_CALAMITY(" Natural Calamity"),OTHERS("Others");

    private String description;

    AccidentTypes(String description){
        this.description = description;
    }
    @Override
    public String toString() {
        return  description;
    }


     public static List<Map<String,Object>> getAllAccidentTypes(){
        return Arrays.asList(AccidentTypes.values()).parallelStream().map(new Function<AccidentTypes,Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(AccidentTypes accidentTypes) {
                Map<String,Object> accidentTypeMap = Maps.newLinkedHashMap();
                accidentTypeMap.put("accidentType",accidentTypes.name());
                accidentTypeMap.put("description",accidentTypes.toString());
                return accidentTypeMap;
            }
        }).collect(Collectors.toList());
    }



}
