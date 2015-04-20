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
public enum ModalFactorItem {

    SEMI_ANNUAL("Semi-annual Modal Factor"), QUARTERLY("Quarterly Modal Factor"),
    MONTHLY("Monthly Modal Factor");

    private String description;

    ModalFactorItem(String description) {
        this.description = description;
    }

    public static List<Map<String,String>> getModalFactorItems(){
        List<Map<String,String>> definedOrganizationInformationItem = Lists.newArrayList();
        for (ModalFactorItem modalFactorItem : values()){
            Map<String,String> modalFactorMap = Maps.newLinkedHashMap();
            modalFactorMap.put("item",modalFactorItem.name());
            modalFactorMap.put("description",modalFactorItem.description);
            definedOrganizationInformationItem.add(modalFactorMap);
        }
        return definedOrganizationInformationItem;
    }
}
