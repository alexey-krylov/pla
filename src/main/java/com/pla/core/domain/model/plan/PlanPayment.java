package com.pla.core.domain.model.plan;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@Getter(AccessLevel.PACKAGE)
@Entity
class PlanPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @ElementCollection
    private Collection<MaturityAmount> maturityAmounts;

    @OneToOne(cascade = CascadeType.ALL)
    private PremiumPayment premiumPayment;
    @Column(nullable = false)
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
