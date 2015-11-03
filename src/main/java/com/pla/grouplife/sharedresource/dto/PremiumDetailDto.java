package com.pla.grouplife.sharedresource.dto;

import com.google.common.collect.Sets;
import com.pla.grouplife.quotation.query.PremiumInstallmentDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
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
public class PremiumDetailDto {

    private BigDecimal addOnBenefit;

    private BigDecimal profitAndSolvencyLoading;

    private BigDecimal vat;

    private Integer policyTermValue;

    private BigDecimal annualPremium;

    private BigDecimal semiannualPremium;

    private BigDecimal quarterlyPremium;

    private BigDecimal monthlyPremium;

    private BigDecimal totalPremium;

    private PremiumInstallmentDto premiumInstallment;

    private Set<PremiumInstallmentDto> installments;

    private BigDecimal hivDiscount;

    private BigDecimal valuedClientDiscount;

    private BigDecimal longTermDiscount;

    private PremiumFrequency optedPremiumFrequency;

    private Boolean isPremiumApplicable = Boolean.TRUE;

    public PremiumDetailDto(BigDecimal addOnBenefit, BigDecimal profitAndSolvencyLoading, BigDecimal hivDiscount, BigDecimal valuedClientDiscount, BigDecimal longTermDiscount, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.profitAndSolvencyLoading = profitAndSolvencyLoading;
        this.hivDiscount = hivDiscount;
        this.valuedClientDiscount = valuedClientDiscount;
        this.longTermDiscount = longTermDiscount;
        this.policyTermValue = policyTermValue;
    }

    public PremiumDetailDto(BigDecimal addOnBenefit, Integer policyTermValue, BigDecimal hivDiscount, BigDecimal valuedClientDiscount, BigDecimal longTermDiscount) {
        this.addOnBenefit = addOnBenefit;
        this.hivDiscount = hivDiscount;
        this.valuedClientDiscount = valuedClientDiscount;
        this.longTermDiscount = longTermDiscount;
        this.policyTermValue = policyTermValue;
    }

    public PremiumDetailDto addOptedInstallmentDetail(int noOfInstallment, BigDecimal installmentAmount) {
        this.premiumInstallment = new com.pla.grouplife.quotation.query.PremiumInstallmentDto(noOfInstallment, installmentAmount);
        return this;
    }

    public PremiumDetailDto addInstallments(int noOfInstallment, BigDecimal installmentAmount) {
        if (isEmpty(this.installments)) {
            this.installments = Sets.newHashSet();
        }
        PremiumInstallmentDto premiumInstallment = new PremiumInstallmentDto(noOfInstallment, installmentAmount);
        this.installments.add(premiumInstallment);
        return this;
    }

    public PremiumDetailDto addFrequencyPremiumAmount(BigDecimal annualPremiumAmount, BigDecimal semiannualPremiumAmount, BigDecimal quarterlyPremiumAmount, BigDecimal monthlyPremiumAmount) {
        this.annualPremium = annualPremiumAmount;
        this.semiannualPremium = semiannualPremiumAmount;
        this.quarterlyPremium = quarterlyPremiumAmount;
        this.monthlyPremium = monthlyPremiumAmount;
        return this;
    }

    public PremiumDetailDto addNetTotalPremiumAmount(BigDecimal netTotalAmount) {
        this.totalPremium = netTotalAmount;
        return this;
    }

    public PremiumDetailDto updateWithOptedFrequency(PremiumFrequency optedPremiumFrequency){
        this.optedPremiumFrequency=optedPremiumFrequency;
        return this;
    }
}
