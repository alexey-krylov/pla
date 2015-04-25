package com.pla.quotation.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

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

    private Set<PremiumInstallmentDto> premiumInstallments;

    @Getter
    @Setter
    @NoArgsConstructor
    public class PremiumInstallmentDto {

        private Integer installmentNo;

        private BigDecimal installmentAmount;
    }

}
