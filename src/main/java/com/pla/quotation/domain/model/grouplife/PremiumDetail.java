package com.pla.quotation.domain.model.grouplife;

import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Samir on 4/23/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
public class PremiumDetail {

    private BigDecimal addOnBenefit;

    private BigDecimal profitAndSolvency;

    private BigDecimal discount;

    private BigDecimal vat;

    private Integer policyTermValue;

    private Set<PremiumInstallment> premiumInstallments;

    private Set<Policy> policies;

    PremiumDetail(BigDecimal addOnBenefit, BigDecimal profitAndSolvency, BigDecimal discount, BigDecimal vat, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.profitAndSolvency = profitAndSolvency;
        this.discount = discount;
        this.vat = vat;
        this.policyTermValue = policyTermValue;
    }

    public PremiumDetail addPremiumInstallments(Set<PremiumInstallment> premiumInstallments) {
        this.premiumInstallments = premiumInstallments;
        return this;
    }

    public PremiumDetail addPolicies(Set<Policy> policies) {
        this.policies = policies;
        return this;
    }

    @Getter(value = AccessLevel.PACKAGE)
    @Setter(value = AccessLevel.PACKAGE)
    @EqualsAndHashCode(of = "installmentNo")
    public class PremiumInstallment {

        private Integer installmentNo;

        private BigDecimal installmentAmount;

        public PremiumInstallment(Integer installmentNo, BigDecimal installmentAmount) {
            this.installmentNo = installmentNo;
            this.installmentAmount = installmentAmount;
        }
    }
}
