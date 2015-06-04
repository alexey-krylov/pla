package com.pla.grouphealth.quotation.domain.model;

import com.google.common.collect.Sets;
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
public class GHPremiumDetail {

    private BigDecimal addOnBenefit;

    private BigDecimal profitAndSolvency;

    private BigDecimal discount;

    private Integer policyTermValue;

    private PremiumInstallment premiumInstallment;

    private Set<PremiumInstallment> installments;

    private Set<GHPolicy> policies;

    private BigDecimal netTotalPremium;

    public GHPremiumDetail(BigDecimal addOnBenefit, BigDecimal profitAndSolvency, BigDecimal discount, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.profitAndSolvency = profitAndSolvency;
        this.discount = discount;
        this.policyTermValue = policyTermValue;
    }


    public GHPremiumDetail updateWithNetPremium(BigDecimal netTotalPremium) {
        this.netTotalPremium = netTotalPremium;
        return this;
    }

    public GHPolicy getAnnualPolicy() {
        Optional<GHPolicy> policyOptional = this.policies.stream().filter(new Predicate<GHPolicy>() {
            @Override
            public boolean test(GHPolicy policy) {
                return false;
            }
        }).findAny();
        return policyOptional.isPresent() ? policyOptional.get() : null;
    }

    public GHPremiumDetail nullifyPremiumInstallment() {
        this.premiumInstallment = null;
        this.installments = null;
        return this;
    }

    public GHPremiumDetail nullifyFrequencyPremium() {
        this.policies = null;
        return this;
    }


    public GHPremiumDetail addChoosenPremiumInstallment(Integer noOfInstallment, BigDecimal installmentAmount) {
        PremiumInstallment premiumInstallment = new PremiumInstallment(noOfInstallment, installmentAmount);
        this.premiumInstallment = premiumInstallment;
        return this;
    }

    public GHPremiumDetail addPolicies(Set<GHPolicy> policies) {
        this.policies = policies;
        return this;
    }

    public GHPremiumDetail addInstallments(Integer installmentNo, BigDecimal installmentAmount) {
        PremiumInstallment premiumInstallment = new PremiumInstallment(installmentNo, installmentAmount);
        if (isEmpty(this.installments)) {
            this.installments = Sets.newHashSet();
        }
        this.installments.add(premiumInstallment);
        return this;
    }

    public BigDecimal getAnnualPremiumAmount() {
        GHPolicy policy = getPolicy(PremiumFrequency.ANNUALLY);
        return policy != null ? policy.getPremium() : null;
    }

    public BigDecimal getSemiAnnualPremiumAmount() {
        GHPolicy policy = getPolicy(PremiumFrequency.SEMI_ANNUALLY);
        return policy != null ? policy.getPremium() : null;
    }


    public BigDecimal getQuarterlyPremiumAmount() {
        GHPolicy policy = getPolicy(PremiumFrequency.QUARTERLY);
        return policy != null ? policy.getPremium() : null;
    }

    public BigDecimal getMonthlyPremiumAmount() {
        GHPolicy policy = getPolicy(PremiumFrequency.MONTHLY);
        return policy != null ? policy.getPremium() : null;
    }


    private GHPolicy getPolicy(PremiumFrequency premiumFrequency) {
        if (isEmpty(this.policies)) {
            return null;
        }
        Optional<GHPolicy> policyOptional = this.policies.stream().filter(new Predicate<GHPolicy>() {
            @Override
            public boolean test(GHPolicy policy) {
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
