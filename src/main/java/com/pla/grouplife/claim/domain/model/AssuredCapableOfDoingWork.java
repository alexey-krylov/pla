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
public enum AssuredCapableOfDoingWork {
    YES("Yes"),NO("No");
    private String description;

    AssuredCapableOfDoingWork (String description){
        this.description = description;
    }
    @Override
    public String toString() {
        return  description;
    }
    public static List<Map<String,Object>> getAllCapableWorkTypes(){
        return Arrays.asList(AssuredCapableOfDoingWork.values()).parallelStream().map(new Function<AssuredCapableOfDoingWork,Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(AssuredCapableOfDoingWork assuredCapableOfDoingWork) {
                Map<String,Object> accidentTypeMap = Maps.newLinkedHashMap();
                accidentTypeMap.put("capableOfWork",assuredCapableOfDoingWork.name());
                accidentTypeMap.put("description",assuredCapableOfDoingWork.toString());
                return accidentTypeMap;
            }
        }).collect(Collectors.toList());
    }
    }
