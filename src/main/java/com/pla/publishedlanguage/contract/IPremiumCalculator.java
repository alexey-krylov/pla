package com.pla.publishedlanguage.contract;

import com.pla.publishedlanguage.domain.model.BasicPremiumDto;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

public interface IPremiumCalculator {

    List<ComputedPremiumDto> calculateBasicPremium(PremiumCalculationDto premiumCalculationDto);

    List<ComputedPremiumDto> calculateModalPremium(BasicPremiumDto basicPremiumDto);

    BigDecimal computeProratePremium(PremiumCalculationDto premiumCalculationDto);
}
