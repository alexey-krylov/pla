package com.pla.core.domain.model.plan;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedEntity;
import org.nthdimenzion.utils.UtilValidator;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@Getter(AccessLevel.PACKAGE)
class PremiumPayment extends AbstractAnnotatedEntity {

    private Set<Integer> validTerms;
    private int paymentCutOffAge;

    protected PremiumPayment() {
    }

    PremiumPayment(Set<Integer> validTerms) {
        checkArgument(UtilValidator.isNotEmpty(validTerms));
        this.validTerms = validTerms;
    }

    PremiumPayment(int paymentCutOffAge) {
        checkArgument(paymentCutOffAge > 0);
        this.paymentCutOffAge = paymentCutOffAge;
    }
}
