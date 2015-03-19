package com.pla.core.domain.model.plan;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@Getter(AccessLevel.PACKAGE)
class PlanPayment {

    private Collection<MaturityAmount> maturityAmounts;

    private PremiumPayment premiumPayment;
    private PremiumPaymentTermType premiumPaymentTermType;

    protected PlanPayment() {
        maturityAmounts = new LinkedList<MaturityAmount>();
        premiumPaymentTermType = PremiumPaymentTermType.REGULAR;
    }

    PlanPayment(PremiumPayment premiumPayment) {
        this();
        this.premiumPayment = premiumPayment;
    }

    public void addMaturityAmount(MaturityAmount maturityAmount) {
        maturityAmounts.add(maturityAmount);
    }

    public static enum PremiumPaymentTermType {
        REGULAR, VALUE, AGE
    }
}
