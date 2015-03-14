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
    private Set<Integer> validTerms;
    private int paymentCutOffAge;

    protected PremiumPayment() {
    }

    PremiumPayment(Set<Integer> validTerms, int paymentCutOffAge) {
        checkArgument(UtilValidator.isNotEmpty(validTerms));
        checkArgument(paymentCutOffAge > 0);
        long termsGreaterThanCutOffAgeAge = validTerms.stream().filter(term -> term.intValue() > paymentCutOffAge).count();
        checkArgument(termsGreaterThanCutOffAgeAge == 0, " Invalid Terms. The values cannot be greater than %d.", paymentCutOffAge);

        this.validTerms = validTerms;
        this.paymentCutOffAge = paymentCutOffAge;
    }

    PremiumPayment(int paymentCutOffAge) {
        checkArgument(paymentCutOffAge > 0);
        this.paymentCutOffAge = paymentCutOffAge;
    }
}
