/*
 * Copyright (c) 3/25/15 8:47 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.plan.premium;

import com.pla.core.domain.model.generalinformation.DiscountFactorOrganizationInformation;
import com.pla.core.domain.model.generalinformation.ModelFactorOrganizationInformation;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.domain.model.PremiumFactor;
import com.pla.sharedkernel.domain.model.PremiumRateFrequency;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PremiumId;
import lombok.*;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 25/03/2015
 */
@Document(collection = "premium")
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"premiumId", "planId", "premiumCategory"})
public class Premium {

    @Id
    private PremiumId premiumId;

    private PlanId planId;

    private CoverageId coverageId;

    @Getter
    private Set<PremiumItem> premiumItems;

    private LocalDate effectiveFrom;

    private LocalDate validTill;

    private PremiumFactor premiumFactor;

    private PremiumRateFrequency premiumRateFrequency;

    @Getter
    private List<PremiumInfluencingFactor> premiumInfluencingFactors;

    private Premium(PremiumId premiumId, PlanId planId, LocalDate effectiveFrom, Set<PremiumItem> premiumItems, PremiumFactor premiumFactor, PremiumRateFrequency premiumRateFrequency, List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        checkArgument(premiumId != null);
        checkArgument(planId != null);
        checkArgument(effectiveFrom != null);
        checkArgument(isNotEmpty(premiumItems));
        checkArgument(premiumFactor != null);
        checkArgument(premiumRateFrequency != null);
        this.premiumId = premiumId;
        this.planId = planId;
        this.effectiveFrom = effectiveFrom;
        this.premiumItems = premiumItems;
        this.premiumFactor = premiumFactor;
        this.premiumRateFrequency = premiumRateFrequency;
        this.premiumInfluencingFactors = premiumInfluencingFactors;
    }

    public static Premium createPremiumWithPlan(PremiumId premiumId, PlanId planId, LocalDate effectiveFrom, List<Map<Map<PremiumInfluencingFactor, String>, Double>> premiumExcelLineItems, PremiumFactor premiumFactor, PremiumRateFrequency premiumRateFrequency, List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        Set<PremiumItem> premiumItems = createPremiumItems(premiumExcelLineItems);
        return new Premium(premiumId, planId, effectiveFrom, premiumItems, premiumFactor, premiumRateFrequency, premiumInfluencingFactors);
    }

    public static Premium createPremiumWithPlanAndCoverage(PremiumId premiumId, PlanId planId, CoverageId coverageId, LocalDate effectiveFrom, List<Map<Map<PremiumInfluencingFactor, String>, Double>> premiumExcelLineItems, PremiumFactor premiumFactor, PremiumRateFrequency premiumRateFrequency, List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        Set<PremiumItem> premiumItems = createPremiumItems(premiumExcelLineItems);
        Premium premium = new Premium(premiumId, planId, effectiveFrom, premiumItems, premiumFactor, premiumRateFrequency, premiumInfluencingFactors);
        premium.coverageId = coverageId;
        return premium;
    }

    private static Set<PremiumItem> createPremiumItems(List<Map<Map<PremiumInfluencingFactor, String>, Double>> premiumExcelLineItems) {
        Set<PremiumItem> premiumItemSet = premiumExcelLineItems.stream().map(new TransformToPremiumItem()).collect(Collectors.toSet());
        return premiumItemSet;
    }

    public Premium expirePremium(LocalDate validTill) {
        this.validTill = validTill;
        return this;
    }

    private static class TransformToPremiumItem implements Function<Map<Map<PremiumInfluencingFactor, String>, Double>, PremiumItem> {

        @Override
        public PremiumItem apply(Map<Map<PremiumInfluencingFactor, String>, Double> premiumExcelLineItem) {
            return PremiumItem.createCoveragePremiumItem(premiumExcelLineItem);
        }
    }

    public BigDecimal getProratePremium(PremiumItem premiumItem, int noOfDays, BigDecimal sumAssured) {
        return getAllowedPremiumAmount(premiumItem, noOfDays,sumAssured );
    }

    public BigDecimal getAnnualPremium(PremiumItem premiumItem, Set<DiscountFactorOrganizationInformation> discountFactorItems, int noOfDays, BigDecimal sumAssured) {
        BigDecimal premiumAmount = getAllowedPremiumAmount(premiumItem, noOfDays,sumAssured );
        if (PremiumRateFrequency.YEARLY.equals(this.premiumRateFrequency)) {
            return premiumAmount;
        }
        premiumAmount = premiumAmount.multiply(DiscountFactorOrganizationInformation.getAnnualDiscountFactor(discountFactorItems));
        return premiumAmount;
    }

    public BigDecimal getMonthlyPremium(PremiumItem premiumItem, Set<ModelFactorOrganizationInformation> modelFactorItems, int noOfDays, BigDecimal sumAssured) {
        BigDecimal premiumAmount = getAllowedPremiumAmount(premiumItem, noOfDays,sumAssured );
        if (PremiumRateFrequency.MONTHLY.equals(premiumRateFrequency)) {
            return premiumAmount;
        }
        premiumAmount = premiumAmount.multiply(ModelFactorOrganizationInformation.getMonthlyModalFactor(modelFactorItems));
        return premiumAmount;
    }

    public BigDecimal getQuarterlyPremium(PremiumItem premiumItem, Set<ModelFactorOrganizationInformation> modelFactorItems, Set<DiscountFactorOrganizationInformation> discountFactorItems, int noOfDays, BigDecimal sumAssured) {
        BigDecimal premiumAmount = getAllowedPremiumAmount(premiumItem, noOfDays,sumAssured );
        if (PremiumRateFrequency.MONTHLY.equals(premiumRateFrequency)) {
            premiumAmount = premiumAmount.multiply(DiscountFactorOrganizationInformation.getQuarterlyDiscountFactor(discountFactorItems));
            return premiumAmount;
        }
        premiumAmount = premiumAmount.multiply(ModelFactorOrganizationInformation.getQuarterlyModalFactor(modelFactorItems));
        return premiumAmount;
    }

    public BigDecimal getSemiAnnuallyPremium(PremiumItem premiumItem, Set<ModelFactorOrganizationInformation> modelFactorItems, Set<DiscountFactorOrganizationInformation> discountFactorItems, int noOfDays, BigDecimal sumAssured) {
        BigDecimal premiumAmount = getAllowedPremiumAmount(premiumItem, noOfDays,sumAssured );
        if (PremiumRateFrequency.MONTHLY.equals(premiumRateFrequency)) {
            premiumAmount = premiumAmount.multiply(DiscountFactorOrganizationInformation.getSemiAnnualDiscountFactor(discountFactorItems));
            return premiumAmount;
        }
        premiumAmount = premiumAmount.multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems));
        return premiumAmount;
    }


    private BigDecimal getAllowedPremiumAmount(PremiumItem premiumItem, int noOfDays, BigDecimal sumAssured) {
        BigDecimal premiumAmount = premiumItem.getPremium();
        if (PremiumFactor.PER_THOUSAND.equals(premiumFactor)) {
            premiumAmount = sumAssured.multiply(premiumItem.getPremium());
            premiumAmount = premiumAmount.divide(new BigDecimal(1000));
        }
        if (noOfDays != 365) {
            if (PremiumRateFrequency.MONTHLY.equals(premiumRateFrequency)) {
                premiumAmount = premiumAmount.divide(new BigDecimal(30), 4, BigDecimal.ROUND_CEILING);
            } else if (PremiumRateFrequency.YEARLY.equals(premiumRateFrequency)) {
                premiumAmount = premiumAmount.divide(new BigDecimal(365), 4, BigDecimal.ROUND_CEILING);
            }
            premiumAmount = premiumAmount.multiply(new BigDecimal(noOfDays));
        }
        return premiumAmount;
    }

    public boolean
    hasAllInfluencingFactor(List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        return this.premiumInfluencingFactors.containsAll(premiumInfluencingFactors);
    }
}
