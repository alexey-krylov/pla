package com.pla.grouphealth.sharedresource.dto;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GHPremiumDetailDto {

    private BigDecimal addOnBenefit;

    private BigDecimal profitAndSolvencyLoading;

    private BigDecimal discounts;

    private BigDecimal vat;

    private Integer policyTermValue;

    private BigDecimal annualPremium;

    private BigDecimal semiannualPremium;

    private BigDecimal quarterlyPremium;

    private BigDecimal monthlyPremium;

    private BigDecimal totalPremium;

    private PremiumInstallmentDto premiumInstallment;

    private Set<PremiumInstallmentDto> installments;

    private BigDecimal waiverOfExcessLoading;


    public GHPremiumDetailDto(BigDecimal addOnBenefit, BigDecimal profitAndSolvencyLoading, BigDecimal discounts, BigDecimal waiverOfExcessLoading, BigDecimal vat, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.profitAndSolvencyLoading = profitAndSolvencyLoading;
        this.discounts = discounts;
        this.policyTermValue = policyTermValue;
        this.waiverOfExcessLoading = waiverOfExcessLoading;
        this.vat = vat;
    }

    public GHPremiumDetailDto(BigDecimal addOnBenefit, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.policyTermValue = policyTermValue;

    }

    public GHPremiumDetailDto(BigDecimal addOnBenefit, Integer policyTermValue, BigDecimal waiverOfExcessLoading, BigDecimal vat) {
        this.addOnBenefit = addOnBenefit;
        this.policyTermValue = policyTermValue;
        this.waiverOfExcessLoading = waiverOfExcessLoading;
        this.vat = vat;
    }


    public GHPremiumDetailDto addOptedInstallmentDetail(int noOfInstallment, BigDecimal installmentAmount) {
        this.premiumInstallment = new PremiumInstallmentDto(noOfInstallment, installmentAmount);
        return this;
    }

    public GHPremiumDetailDto addInstallments(int noOfInstallment, BigDecimal installmentAmount) {
        if (isEmpty(this.installments)) {
            this.installments = Sets.newHashSet();
        }
        PremiumInstallmentDto premiumInstallment = new PremiumInstallmentDto(noOfInstallment, installmentAmount);
        this.installments.add(premiumInstallment);
        return this;
    }

    public GHPremiumDetailDto addFrequencyPremiumAmount(BigDecimal annualPremiumAmount, BigDecimal semiannualPremiumAmount, BigDecimal quarterlyPremiumAmount, BigDecimal monthlyPremiumAmount) {
        this.annualPremium = annualPremiumAmount;
        this.semiannualPremium = semiannualPremiumAmount;
        this.quarterlyPremium = quarterlyPremiumAmount;
        this.monthlyPremium = monthlyPremiumAmount;
        return this;
    }

    public GHPremiumDetailDto addNetTotalPremiumAmount(BigDecimal netTotalAmount) {
        this.totalPremium = netTotalAmount;
        return this;
    }
}
