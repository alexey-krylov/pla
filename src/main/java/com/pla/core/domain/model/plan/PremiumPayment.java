package com.pla.core.domain.model.plan;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@Getter(AccessLevel.PACKAGE)
@Entity
class PremiumPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @ElementCollection
    @CollectionTable(name = "premium_payment_values")
    private Set<Integer> validTerms;
    private int paymentCutOffAge;

    protected PremiumPayment() {
    }

    //TODO Have to remove PaymentCutOffAge
    PremiumPayment(Set<Integer> validTerms) {
        checkArgument(UtilValidator.isNotEmpty(validTerms));
        this.validTerms = validTerms;
    }

    PremiumPayment(int paymentCutOffAge) {
        checkArgument(paymentCutOffAge > 0);
        this.paymentCutOffAge = paymentCutOffAge;
    }
}
