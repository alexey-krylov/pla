package com.pla.core.surrendervalue.domain.model;

import com.google.common.collect.Maps;
import lombok.Getter;
import java.util.function.Function;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ak on 4/2/2016.
 */
@Getter
public enum PolicyYearExpression {

    SINGLE("Single"),RANGE("Range");

    PolicyYearExpression(String description){
        this.description = description;
    }
    private String description;

    @Override
    public String toString() {
        return  description;
    }


    public static List<Map<String,Object>> getPolicyYearExpression(){
        return Arrays.asList(PolicyYearExpression.values()).parallelStream().map(new Function<PolicyYearExpression, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(PolicyYearExpression policyYearExpression) {
                Map<String,Object> policyYearExpressionMap = Maps.newLinkedHashMap();
                policyYearExpressionMap.put("policyYearExpression",policyYearExpression.name());
                policyYearExpressionMap.put("description",policyYearExpression.toString());
                return policyYearExpressionMap;
            }
        }).collect(Collectors.toList());
    }

}
