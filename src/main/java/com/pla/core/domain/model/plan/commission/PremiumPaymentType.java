package com.pla.core.domain.model.plan.commission;

import lombok.Getter;

/**
 * Created by Admin on 06-Jan-16.
 */
@Getter
public enum PremiumPaymentType {

    SINGLE_PREMIUM("Single Premium"),OTHER_PREMIUMS("Other Premiums");

    private String description;

    PremiumPaymentType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
