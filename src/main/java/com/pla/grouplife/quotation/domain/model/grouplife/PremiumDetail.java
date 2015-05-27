package com.pla.grouplife.quotation.domain.model.grouplife;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by Samir on 4/23/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class PremiumDetail {

    private BigDecimal addOnBenefit;

    private BigDecimal profitAndSolvency;

    private BigDecimal discount;

    private Integer policyTermValue;

    private PremiumInstallment premiumInstallment;

    private Set<Policy> policies;

    private BigDecimal netTotalPremium;

    public PremiumDetail(BigDecimal addOnBenefit, BigDecimal profitAndSolvency, BigDecimal discount, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.profitAndSolvency = profitAndSolvency;
        this.discount = discount;
        this.policyTermValue = policyTermValue;
    }


    public PremiumDetail updateWithNetPremium(BigDecimal netTotalPremium) {
        this.netTotalPremium = netTotalPremium;
        return this;
    }

    public Policy getAnnualPolicy() {
        Optional<Policy> policyOptional = this.policies.stream().filter(new Predicate<Policy>() {
            @Override
            public boolean test(Policy policy) {
                return false;
            }
        }).findAny();
        return policyOptional.isPresent() ? policyOptional.get() : null;
    }

    public PremiumDetail addPremiumInstallment(int noOfInstallment, BigDecimal installmentAmount) {
        PremiumInstallment premiumInstallment = new PremiumInstallment(noOfInstallment, installmentAmount);
        this.premiumInstallment = premiumInstallment;
        return this;
    }

    public PremiumDetail addPolicies(Set<Policy> policies) {
        this.policies = policies;
        return this;
    }

    public BigDecimal getAnnualPremiumAmount() {
        Policy policy = getPolicy(PremiumFrequency.ANNUALLY);
        return policy != null ? policy.getPremium() : null;
    }

    public BigDecimal getSemiAnnualPremiumAmount() {
        Policy policy = getPolicy(PremiumFrequency.SEMI_ANNUALLY);
        return policy != null ? policy.getPremium() : null;
    }


    public BigDecimal getQuarterlyPremiumAmount() {
        Policy policy = getPolicy(PremiumFrequency.QUARTERLY);
        return policy != null ? policy.getPremium() : null;
    }

    public BigDecimal getMonthlyPremiumAmount() {
        Policy policy = getPolicy(PremiumFrequency.MONTHLY);
        return policy != null ? policy.getPremium() : null;
    }


    private Policy getPolicy(PremiumFrequency premiumFrequency) {
        Optional<Policy> policyOptional = this.policies.stream().filter(new Predicate<Policy>() {
            @Override
            public boolean test(Policy policy) {
                return premiumFrequency.equals(policy.getPremiumFrequency());
            }
        }).findAny();
        return policyOptional.isPresent() ? policyOptional.get() : null;
    }

    @Getter
    public class PremiumInstallment {

        private int noOfInstallment;

        private BigDecimal installmentAmount;

        PremiumInstallment(int noOfInstallment, BigDecimal installmentAmount) {
            this.noOfInstallment = noOfInstallment;
            this.installmentAmount = installmentAmount;
        }

    }
}
