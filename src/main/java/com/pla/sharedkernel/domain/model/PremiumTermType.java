package com.pla.sharedkernel.domain.model;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 21/03/2015
 */
public enum PremiumTermType {
    REGULAR, SPECIFIED_VALUES, SPECIFIED_AGES, SINGLE,
    SINGLE_REGULAR,SINGLE_SPECIFIED_VALUES,
    SINGLE_SPECIFIED_AGES;

    public Set<String> getSheetNamesByPremiumTermType() {
        String premiumTermTypeString = this.name();
        switch(premiumTermTypeString){
            case "REGULAR" :
                return  Sets.newHashSet("REGULAR");
            case "SPECIFIED_VALUES" :
                return  Sets.newHashSet("SPECIFIED_VALUES");
            case "SPECIFIED_AGES" :
                return  Sets.newHashSet("SPECIFIED_AGES");
            case "SINGLE" :
                return  Sets.newHashSet("SINGLE");
            case "SINGLE_REGULAR" :
                return  Sets.newHashSet("SINGLE","REGULAR");
            case "SINGLE_SPECIFIED_VALUES" :
                return  Sets.newHashSet("SINGLE","SPECIFIED_VALUES");
            case "SINGLE_SPECIFIED_AGES" :
                return  Sets.newHashSet("SINGLE","SPECIFIED_AGES");
        }
        return Collections.emptySet();
    }
}
