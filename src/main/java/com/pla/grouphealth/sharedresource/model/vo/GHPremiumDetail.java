package com.pla.grouphealth.sharedresource.model.vo;

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

    private BigDecimal waiverOfExcessLoading;

    private BigDecimal discount;

    private Integer policyTermValue;

    private PremiumInstallment premiumInstallment;

    private Set<PremiumInstallment> installments;

    private Set<GHFrequencyPremium> frequencyPremiums;

    private GHFrequencyPremium optedFrequencyPremium;

    private BigDecimal netTotalPremium;

    private BigDecimal vat;

    public GHPremiumDetail(BigDecimal addOnBenefit, BigDecimal profitAndSolvency, BigDecimal discount, BigDecimal waiverOfExcessLoading, BigDecimal vat, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.profitAndSolvency = profitAndSolvency;
        this.discount = discount;
        this.policyTermValue = policyTermValue;
        this.waiverOfExcessLoading = waiverOfExcessLoading;
        this.vat = vat;
    }


    public GHPremiumDetail updateWithNetPremium(BigDecimal netTotalPremium) {
        this.netTotalPremium = netTotalPremium;
        return this;
    }

    public GHPremiumDetail updateWithOptedFrequencyPremium(PremiumFrequency premiumFrequency) {

        Optional<GHFrequencyPremium> policyOptional = this.frequencyPremiums.stream().filter(new Predicate<GHFrequencyPremium>() {
            @Override
            public boolean test(GHFrequencyPremium policy) {
                return premiumFrequency.equals(policy.getPremiumFrequency());
            }
        }).findAny();
        GHFrequencyPremium ghFrequencyPremium = policyOptional.isPresent() ? policyOptional.get() : null;
        this.optedFrequencyPremium = ghFrequencyPremium;
        return this;
    }


    public GHPremiumDetail nullifyPremiumInstallment() {
        this.premiumInstallment = null;
        this.installments = null;
        return this;
    }

    public GHPremiumDetail nullifyFrequencyPremium() {
        this.frequencyPremiums = null;
        this.optedFrequencyPremium=null;
        return this;
    }


    public GHPremiumDetail addChoosenPremiumInstallment(Integer noOfInstallment, BigDecimal installmentAmount) {
        PremiumInstallment premiumInstallment = new PremiumInstallment(noOfInstallment, installmentAmount);
        this.premiumInstallment = premiumInstallment;
        return this;
    }

    public GHPremiumDetail addPolicies(Set<GHFrequencyPremium> policies) {
        this.frequencyPremiums = policies;
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
        GHFrequencyPremium policy = getPolicy(PremiumFrequency.ANNUALLY);
        return policy != null ? policy.getPremium() : null;
    }

    public BigDecimal getSemiAnnualPremiumAmount() {
        GHFrequencyPremium policy = getPolicy(PremiumFrequency.SEMI_ANNUALLY);
        return policy != null ? policy.getPremium() : null;
    }


    public BigDecimal getQuarterlyPremiumAmount() {
        GHFrequencyPremium policy = getPolicy(PremiumFrequency.QUARTERLY);
        return policy != null ? policy.getPremium() : null;
    }

    public BigDecimal getMonthlyPremiumAmount() {
        GHFrequencyPremium policy = getPolicy(PremiumFrequency.MONTHLY);
        return policy != null ? policy.getPremium() : null;
    }


    private GHFrequencyPremium getPolicy(PremiumFrequency premiumFrequency) {
        if (isEmpty(this.frequencyPremiums)) {
            return null;
        }
        Optional<GHFrequencyPremium> policyOptional = this.frequencyPremiums.stream().filter(new Predicate<GHFrequencyPremium>() {
            @Override
            public boolean test(GHFrequencyPremium policy) {
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
