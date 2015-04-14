package com.pla.publishedlanguage.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/10/2015.
 */
@Getter
public class PremiumCalculationDto {

    private PlanId planId;

    private CoverageId coverageId;

    private Set<PremiumCalculationInfluencingFactorItem> premiumCalculationInfluencingFactorItems;

    private LocalDate calculateAsOf;

    private PremiumFrequency premiumFrequency;

    public PremiumCalculationDto(PlanId planId, CoverageId coverageId, Set<PremiumCalculationInfluencingFactorItem> premiumCalculationInfluencingFactorItems, LocalDate calculateAsOf, PremiumFrequency premiumFrequency) {
        checkArgument(planId != null);
        checkArgument(calculateAsOf != null);
        checkArgument(isNotEmpty(premiumCalculationInfluencingFactorItems));
        this.calculateAsOf = calculateAsOf;
        this.premiumFrequency = premiumFrequency;
        this.planId = planId;
        this.coverageId = coverageId;
        this.premiumCalculationInfluencingFactorItems = premiumCalculationInfluencingFactorItems;
    }

    @Getter
    public static class PremiumCalculationInfluencingFactorItem {

        private PremiumInfluencingFactor premiumInfluencingFactor;

        private String value;

        public PremiumCalculationInfluencingFactorItem(PremiumInfluencingFactor premiumInfluencingFactor, String value) {
            checkArgument(premiumInfluencingFactor != null);
            checkArgument(isNotEmpty(value));
            this.premiumInfluencingFactor = premiumInfluencingFactor;
            this.value = value;
        }
    }
}
