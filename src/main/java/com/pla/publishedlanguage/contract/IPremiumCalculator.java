package com.pla.publishedlanguage.contract;

import com.pla.publishedlanguage.domain.model.BasicPremiumDto;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

public interface IPremiumCalculator {

    List<ComputedPremiumDto> calculateBasicPremium(PremiumCalculationDto premiumCalculationDto, BigDecimal sumAssured, LineOfBusinessEnum lineOfBusinessEnum, boolean compoundPremiumType, String premiumPaymentType);

    List<ComputedPremiumDto> calculateBasicPremiumWithPolicyFee(PremiumCalculationDto premiumCalculationDto, BigDecimal sumAssured, boolean compoundPremiumType, String premiumPaymentType);

    List<ComputedPremiumDto> calculateModalPremium(BasicPremiumDto basicPremiumDto);

    BigDecimal computeProratePremium(PremiumCalculationDto premiumCalculationDto, BigDecimal sumAssured);

    List<PremiumInfluencingFactor> getPremiumInfluencingFactors(PlanId planId, LocalDate calculateDate);

    List<PremiumInfluencingFactor> getPremiumInfluencingFactors(PlanId planId, CoverageId coverageId, LocalDate calculateDate);

}
