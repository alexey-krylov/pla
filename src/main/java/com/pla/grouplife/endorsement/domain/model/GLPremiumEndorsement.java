package com.pla.grouplife.endorsement.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class GLPremiumEndorsement {

    private BigDecimal oldPremium;

    private BigDecimal newPremium;

    public GLPremiumEndorsement(BigDecimal oldPremium, BigDecimal newPremium) {
        this.oldPremium = oldPremium;
        this.newPremium = newPremium;
    }
}
