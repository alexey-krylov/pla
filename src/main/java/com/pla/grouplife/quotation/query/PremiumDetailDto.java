package com.pla.grouplife.quotation.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class PremiumDetailDto {

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


    public PremiumDetailDto(BigDecimal addOnBenefit, BigDecimal profitAndSolvencyLoading, BigDecimal discounts, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.profitAndSolvencyLoading = profitAndSolvencyLoading;
        this.discounts = discounts;
        this.policyTermValue = policyTermValue;
    }

    public PremiumDetailDto(BigDecimal addOnBenefit, Integer policyTermValue) {
        this.addOnBenefit = addOnBenefit;
        this.policyTermValue = policyTermValue;

    }

    public PremiumDetailDto addInstallmentDetail(int noOfInstallment, BigDecimal installmentAmount) {
        this.premiumInstallment = new PremiumInstallmentDto(noOfInstallment, installmentAmount);
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

    @Getter
    @Setter
    @NoArgsConstructor
    public class PremiumInstallmentDto {

        private Integer installmentNo;

        private BigDecimal installmentAmount;

        public PremiumInstallmentDto(int installmentNo, BigDecimal installmentAmount) {
            this.installmentNo = installmentNo;
            this.installmentAmount = installmentAmount;
        }
    }

}
