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
public enum ApplyConditionTypes {
    YES("Yes"),NO("No");

    private String description;

    ApplyConditionTypes(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }

     public static List<Map<String,Object>> getApplyConditionTypes(){
        return Arrays.asList(ApplyConditionTypes.values()).parallelStream().map(new Function<ApplyConditionTypes,Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(ApplyConditionTypes applyConditionTypes) {
                Map<String,Object> applyConditionTypeMap = Maps.newLinkedHashMap();
                applyConditionTypeMap .put("conditionType",applyConditionTypes.name());
                applyConditionTypeMap .put("description",applyConditionTypes.toString());
                return applyConditionTypeMap ;
            }
        }).collect(Collectors.toList());
    }


}
