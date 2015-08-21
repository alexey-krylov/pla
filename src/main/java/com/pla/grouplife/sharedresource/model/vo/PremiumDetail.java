package com.pla.grouplife.sharedresource.model.vo;

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
public class PremiumDetail {

    private BigDecimal addOnBenefit;

    private BigDecimal profitAndSolvency;

    private Integer policyTermValue;

    private PremiumInstallment premiumInstallment;

    private Set<PremiumInstallment> installments;

    private Set<GLFrequencyPremium> frequencyPremiums;

    private BigDecimal netTotalPremium;

    private BigDecimal hivDiscount;

    private BigDecimal valuedClientDiscount;

    private BigDecimal longTermDiscount;

    private GLFrequencyPremium optedFrequencyPremium;


    public PremiumDetail(BigDecimal addOnBenefit, BigDecimal profitAndSolvency, BigDecimal hivDiscount, BigDecimal valuedClientDiscount, BigDecimal longTermDiscount, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.profitAndSolvency = profitAndSolvency;
        this.hivDiscount = hivDiscount;
        this.valuedClientDiscount = valuedClientDiscount;
        this.longTermDiscount = longTermDiscount;
        this.policyTermValue = policyTermValue;
    }


    public PremiumDetail updateWithNetPremium(BigDecimal netTotalPremium) {
        this.netTotalPremium = netTotalPremium;
        return this;
    }

    public GLFrequencyPremium getAnnualPolicy() {
        Optional<GLFrequencyPremium> policyOptional = this.frequencyPremiums.stream().filter(new Predicate<GLFrequencyPremium>() {
            @Override
            public boolean test(GLFrequencyPremium policy) {
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
        this.frequencyPremiums = null;
        this.optedFrequencyPremium = null;
        return this;
    }


    public PremiumDetail addChoosenPremiumInstallment(Integer noOfInstallment, BigDecimal installmentAmount) {
        PremiumInstallment premiumInstallment = new PremiumInstallment(noOfInstallment, installmentAmount);
        this.premiumInstallment = premiumInstallment;
        return this;
    }

    public PremiumDetail addPolicies(Set<GLFrequencyPremium> policies) {
        this.frequencyPremiums = policies;
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
        GLFrequencyPremium frequencyPremium = getPolicy(PremiumFrequency.ANNUALLY);
        return frequencyPremium != null ? frequencyPremium.getPremium() : null;
    }

    public BigDecimal getSemiAnnualPremiumAmount() {
        GLFrequencyPremium frequencyPremium = getPolicy(PremiumFrequency.SEMI_ANNUALLY);
        return frequencyPremium != null ? frequencyPremium.getPremium() : null;
    }


    public BigDecimal getQuarterlyPremiumAmount() {
        GLFrequencyPremium frequencyPremium = getPolicy(PremiumFrequency.QUARTERLY);
        return frequencyPremium != null ? frequencyPremium.getPremium() : null;
    }

    public BigDecimal getMonthlyPremiumAmount() {
        GLFrequencyPremium frequencyPremium = getPolicy(PremiumFrequency.MONTHLY);
        return frequencyPremium != null ? frequencyPremium.getPremium() : null;
    }

    public PremiumDetail updateWithOptedFrequencyPremium(PremiumFrequency premiumFrequency) {

        Optional<GLFrequencyPremium> policyOptional = this.frequencyPremiums.stream().filter(new Predicate<GLFrequencyPremium>() {
            @Override
            public boolean test(GLFrequencyPremium policy) {
                return premiumFrequency.equals(policy.getPremiumFrequency());
            }
        }).findAny();
        GLFrequencyPremium ghFrequencyPremium = policyOptional.isPresent() ? policyOptional.get() : null;
        this.optedFrequencyPremium = ghFrequencyPremium;
        return this;
    }


    private GLFrequencyPremium getPolicy(PremiumFrequency premiumFrequency) {
        if (isEmpty(this.frequencyPremiums)) {
            return null;
        }
        Optional<GLFrequencyPremium> policyOptional = this.frequencyPremiums.stream().filter(new Predicate<GLFrequencyPremium>() {
            @Override
            public boolean test(GLFrequencyPremium policy) {
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