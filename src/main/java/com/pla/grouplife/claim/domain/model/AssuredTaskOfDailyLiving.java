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
public enum AssuredTaskOfDailyLiving {
    DRESSING("Dressing"), USING_THE_TOILET("Using The Toilet"),WALKING ("Walking"),FEEDING_HIM_HERSELF(" Feeding Him Herself"),USING_TELEPHONE(" Using Telephone"), BATHING("Bathing"),TAKING_MEDICATION(" Taking Medication");
    private String description;

    AssuredTaskOfDailyLiving  (String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }


    public static List<Map<String,Object>> getAllAssuredTaskTypes(){
        return Arrays.asList(AssuredTaskOfDailyLiving.values()).parallelStream().map(new Function<AssuredTaskOfDailyLiving,Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(AssuredTaskOfDailyLiving assuredTask) {
                Map<String,Object> assuredTaskMap = Maps.newLinkedHashMap();
                assuredTaskMap.put("assuredTaskType",assuredTask.name());
                assuredTaskMap.put("description",assuredTask.toString());
                return assuredTaskMap;
            }
        }).collect(Collectors.toList());
    }


}
