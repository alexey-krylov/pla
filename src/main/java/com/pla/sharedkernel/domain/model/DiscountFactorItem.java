package com.pla.sharedkernel.domain.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum DiscountFactorItem {

    ANNUAL("Annual Discount Factor"),
    SEMI_ANNUAL("Semi - annual Discount Factor"),
    QUARTERLY("Quarterly Discount Factor");

    private String description;

    DiscountFactorItem(String description) {
        this.description = description;
    }

    public static List<Map<String,String>> getDiscountFactorItems(){
        List<Map<String,String>> definedOrganizationInformationItem = Lists.newArrayList();
        for (DiscountFactorItem discountFactorItem : values()){
            Map<String,String> discountFactorMap = Maps.newLinkedHashMap();
            discountFactorMap.put("item",discountFactorItem.name());
            discountFactorMap.put("description", discountFactorItem.description);
            definedOrganizationInformationItem.add(discountFactorMap);
        }
        return definedOrganizationInformationItem;
    }

}
