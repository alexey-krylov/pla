package com.pla.publishedlanguage.domain.model;

import com.google.common.collect.Sets;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
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

    private int noOfDays;

    public PremiumCalculationDto(PlanId planId, CoverageId coverageId, LocalDate calculateAsOf, PremiumFrequency premiumFrequency, int noOfDays) {
        checkArgument(planId != null);
        checkArgument(calculateAsOf != null);
        this.calculateAsOf = calculateAsOf;
        this.premiumFrequency = premiumFrequency;
        this.planId = planId;
        this.coverageId = coverageId;
        this.noOfDays = noOfDays;
    }

    public PremiumCalculationDto addInfluencingFactorItemValue(PremiumInfluencingFactor premiumInfluencingFactor, String value) {
        if (isEmpty(this.premiumCalculationInfluencingFactorItems)) {
            this.premiumCalculationInfluencingFactorItems = Sets.newHashSet();
        }
        PremiumCalculationInfluencingFactorItem premiumCalculationInfluencingFactorItem = new PremiumCalculationInfluencingFactorItem(premiumInfluencingFactor, value);
        this.premiumCalculationInfluencingFactorItems.add(premiumCalculationInfluencingFactorItem);
        return this;
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

    public List<PremiumInfluencingFactor> getInfluencingFactors() {
        List<PremiumInfluencingFactor> premiumInfluencingFactorList = this.premiumCalculationInfluencingFactorItems.stream().map(new Function<PremiumCalculationInfluencingFactorItem, PremiumInfluencingFactor>() {
            @Override
            public PremiumInfluencingFactor apply(PremiumCalculationInfluencingFactorItem premiumCalculationInfluencingFactorItem) {
                return premiumCalculationInfluencingFactorItem.getPremiumInfluencingFactor();
            }
        }).collect(Collectors.toList());
        return premiumInfluencingFactorList;
    }
}
