/*
 * Copyright (c) 3/26/15 5:53 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.domain.model;

import com.google.common.collect.Lists;
import org.nthdimenzion.utils.UtilValidator;

import java.util.List;

/**
 * @author: Mohan Sharma
 * @since 1.0 26/03/2015
 */
public enum PremiumType {

    AMOUNT("Amount"){
        @Override
        public String toString() {
            return PremiumType.AMOUNT.description;
        }
    }, RATE("Rate"){
        @Override
        public String toString() {
            return PremiumType.RATE.description;
        }
    };

    private String description;

    PremiumType(String description){
        this.description = description;
    }

    public static List<String> getAllPremiumType() {
        List<String> premiumTypes= Lists.newArrayList();
        for (PremiumType premiumType : PremiumType.values()) {
            premiumTypes.add(premiumType.description);
        }
        return premiumTypes;
    }

    public static boolean checkIfValidConstant(String value) {
        for(PremiumType premiumType : PremiumType.values()){
            if(premiumType.description.equalsIgnoreCase(value))
                return Boolean.TRUE;
        }
        if(UtilValidator.isEmpty(value))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }
}
