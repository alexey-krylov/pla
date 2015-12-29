package com.pla.grouplife.endorsement.domain.model;

import com.pla.grouplife.sharedresource.model.vo.Insured;

import java.math.BigDecimal;

/**
 * Created by Admin on 28-Dec-15.
 */
public class FreeCoverLimitEndorsement {

    private Insured insured;
    private BigDecimal freeCoverLimit;

    public FreeCoverLimitEndorsement(Insured insured,BigDecimal freeCoverLimit) {
        this.insured = insured;
        this.freeCoverLimit  = freeCoverLimit;
    }

}
