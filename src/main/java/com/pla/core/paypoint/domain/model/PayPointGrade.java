package com.pla.core.paypoint.domain.model;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Rudra on 12/11/2015.
 */


public enum  PayPointGrade {
    A("a"),B("b"),C("c");

    private String grade;
    PayPointGrade(String grade){
        this.grade=grade;
    }


    @Override
    public String toString(){
        return grade;
    }
    public  static List<Map<String,Object>> getAllPaypointGrade(){
        return Arrays.asList(PayPointGrade.values()).parallelStream().map(new Function < PayPointGrade, Map < String, Object >>(){
           @Override
        public Map<String,Object>apply(PayPointGrade payPointGrade){
               Map<String ,Object> paypointGradeMap= Maps.newLinkedHashMap();
               paypointGradeMap.put("payPointGrade",payPointGrade.name());
               paypointGradeMap.put("grade",payPointGrade.toString());
               return paypointGradeMap;
           }
        }).collect(Collectors.toList());
    }


}
