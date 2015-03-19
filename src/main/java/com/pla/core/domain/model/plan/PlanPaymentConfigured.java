package com.pla.core.domain.model.plan;

import lombok.Getter;

/**
 * @author: pradyumna
 * @since 1.0 18/03/2015
 */
@Getter
public class PlanPaymentConfigured {
    private PlanPayment planPayment;

    public PlanPaymentConfigured(PlanPayment planPayment) {
        this.planPayment = planPayment;
    }
}
