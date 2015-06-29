package com.pla.grouplife.sharedresource.model.vo;

import com.google.common.collect.Sets;
import com.pla.grouplife.quotation.domain.model.Policy;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

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

    private Set<PremiumInstallment> installments;

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

    public PremiumDetail nullifyPremiumInstallment() {
        this.premiumInstallment = null;
        this.installments = null;
        return this;
    }

    public PremiumDetail nullifyFrequencyPremium() {
        this.policies = null;
        return this;
    }


    public PremiumDetail addChoosenPremiumInstallment(Integer noOfInstallment, BigDecimal installmentAmount) {
        PremiumInstallment premiumInstallment = new PremiumInstallment(noOfInstallment, installmentAmount);
        this.premiumInstallment = premiumInstallment;
        return this;
    }

    public PremiumDetail addPolicies(Set<Policy> policies) {
        this.policies = policies;
        return this;
    }

    public PremiumDetail addInstallments(Integer installmentNo, BigDecimal installmentAmount) {
        PremiumInstallment premiumInstallment = new PremiumInstallment(installmentNo, installmentAmount);
        if (isEmpty(this.installments)) {
            this.installments = Sets.newHashSet();
        }
        this.installments.add(premiumInstallment);
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
        if (isEmpty(this.policies)) {
            return null;
        }
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