package com.pla.core.paypoint.domain.model;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Rudra on 12/11/2015.
 */
@Getter
public enum  PayPointStatus {
    ACTIVE("Active"),INACTIVE("InActive");

    private  String description;

    PayPointStatus(String description){
        this.description=description;
    }

    @Override
    public String toString(){
        return description;
    }
    public  static List<Map<String,Object>> getAllPaypointStatus(){
        return Arrays.asList(PayPointStatus.values()).parallelStream().map(new Function < PayPointStatus, Map < String, Object >>(){
           @Override
        public Map<String,Object>apply(PayPointStatus payPointStatus){
               Map<String ,Object> paypointStatusMap= Maps.newLinkedHashMap();
               paypointStatusMap.put("payPointStatus",payPointStatus.name());
               paypointStatusMap.put("description",payPointStatus.toString());
               return paypointStatusMap;
           }
        }).collect(Collectors.toList());
    }

//    public Set<String> getAllPayPointStatus(){
//        Set<String> payPointStatusSet = Sets.newLinkedHashSet();
//        for(PayPointStatus paypointStatus : PayPointStatus.values()){
//            payPointStatusSet.add(paypointStatus.name());
//        }
//        return payPointStatusSet;
//    }


}
