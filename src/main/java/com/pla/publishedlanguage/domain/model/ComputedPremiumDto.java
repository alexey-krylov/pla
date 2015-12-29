package com.pla.publishedlanguage.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Samir on 4/10/2015.
 */
@Getter
public class ComputedPremiumDto {

    private PremiumFrequency premiumFrequency;

    private BigDecimal premium = BigDecimal.ZERO;

    private BigDecimal policyFee;

    public ComputedPremiumDto(PremiumFrequency premiumFrequency, BigDecimal premium) {
        this.premiumFrequency = premiumFrequency;
        this.premium = premium;
    }

    public ComputedPremiumDto(PremiumFrequency premiumFrequency, BigDecimal premium,BigDecimal policyFee) {
        this(premiumFrequency,premium);
        this.policyFee = policyFee;
    }

    public static BigDecimal getAnnualPremium(List<ComputedPremiumDto> computedPremiumDtoList) {
        return getComputedPremiumByFrequency(computedPremiumDtoList, PremiumFrequency.ANNUALLY).premium;
    }

    public static BigDecimal getSemiAnnualPremium(List<ComputedPremiumDto> computedPremiumDtoList) {
        return getComputedPremiumByFrequency(computedPremiumDtoList, PremiumFrequency.SEMI_ANNUALLY).premium;
    }

    public static BigDecimal getQuarterlyPremium(List<ComputedPremiumDto> computedPremiumDtoList) {
        return getComputedPremiumByFrequency(computedPremiumDtoList, PremiumFrequency.QUARTERLY).premium;
    }

    public static BigDecimal getMonthlyPremium(List<ComputedPremiumDto> computedPremiumDtoList) {
        return getComputedPremiumByFrequency(computedPremiumDtoList, PremiumFrequency.MONTHLY).premium;
    }

    private static ComputedPremiumDto getComputedPremiumByFrequency(List<ComputedPremiumDto> computedPremiumDtoList, PremiumFrequency premiumFrequency) {
        return computedPremiumDtoList.stream().filter(new Predicate<ComputedPremiumDto>() {
            @Override
            public boolean test(ComputedPremiumDto computedPremiumDto) {
                return premiumFrequency.equals(computedPremiumDto.getPremiumFrequency());
            }
        }).findAny().get();
    }

    public static BigDecimal getAnnualPolicyFee(List<ComputedPremiumDto> computedPremiumDtoList) {
        return getComputedPremiumByFrequency(computedPremiumDtoList, PremiumFrequency.ANNUALLY).policyFee;
    }

    public static BigDecimal getSemiAnnualPolicyFee(List<ComputedPremiumDto> computedPremiumDtoList) {
        return getComputedPremiumByFrequency(computedPremiumDtoList, PremiumFrequency.SEMI_ANNUALLY).policyFee;
    }

    public static BigDecimal getQuarterlyFee(List<ComputedPremiumDto> computedPremiumDtoList) {
        return getComputedPremiumByFrequency(computedPremiumDtoList, PremiumFrequency.QUARTERLY).policyFee;
    }

    public static BigDecimal getMonthlyFee(List<ComputedPremiumDto> computedPremiumDtoList) {
        return getComputedPremiumByFrequency(computedPremiumDtoList, PremiumFrequency.MONTHLY).policyFee;
    }



}
