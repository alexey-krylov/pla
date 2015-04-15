package com.pla.publishedlanguage.contract;

import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;

import java.util.List;

/**
 * Author: Nthdimenzion
 */

public interface IPremiumCalculator {

    List<ComputedPremiumDto> calculatePremium(PremiumCalculationDto premiumCalculationDto);
}
