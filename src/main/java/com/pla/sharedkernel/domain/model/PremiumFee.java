package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by User on 4/7/2015.
 */
@Getter
public enum PremiumFee {

    POLICY_FEE("Policy Fee"), INVESTMENT_FEE("Investment Fee"),RISK_PREMIUM("Premium-Policy Fee - Risk Premium");

    private String description;

    PremiumFee(String description) {
        this.description = description;
    }

}
