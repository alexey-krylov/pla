/*
 * Copyright (c) 3/26/15 5:53 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.domain.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author: Samir
 * @since 1.0 26/03/2015
 */
public enum Gender {

    MALE, FEMALE;

    public static List<String> getAllGender() {
        List<String> genders = Lists.newArrayList();
        for (Gender gender : Gender.values()) {
            genders.add(gender.name());
        }
        return genders;
    }
}
