package com.pla.sharedkernel.domain.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Samir on 5/7/2015.
 */
public enum OccupationCategory {

    Management, Staff, Unionized, Contract;

    public static List<String> getAllCategory() {
        List<String> occupationCategories = Lists.newArrayList();
        for (OccupationCategory occupationCategory : OccupationCategory.values()) {
            occupationCategories.add(occupationCategory.name());
        }
        return occupationCategories;
    }
}
