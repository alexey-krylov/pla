package com.pla.sharedkernel.domain.model;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum Tax {

    SERVICE_TAX("Service Tax");

    private String description;

    Tax(String description) {
        this.description = description;
    }

    public static Map<String,String> getServiceTaxItem(){
        Map<String ,String> serviceTaxMap = Maps.newLinkedHashMap();
        serviceTaxMap.put("item",Tax.SERVICE_TAX.name());
        serviceTaxMap.put("",Tax.SERVICE_TAX.getDescription());
        return serviceTaxMap;
    }


}
