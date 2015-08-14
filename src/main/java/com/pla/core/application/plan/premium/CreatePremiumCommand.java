package com.pla.core.application.plan.premium;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.domain.model.PremiumFactor;
import com.pla.sharedkernel.domain.model.PremiumRateFrequency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 4/4/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreatePremiumCommand {

    private List<Map<Map<PremiumInfluencingFactor, String>, Double>> premiumLineItem;
    @NotNull
    private String planId;

    private String coverageId;

    private String premiumInfluencingFactorsStr;

    @NotNull
    private PremiumInfluencingFactor[] premiumInfluencingFactors = new PremiumInfluencingFactor[0];

    @NotNull
    private MultipartFile file;
    @NotNull
    private PremiumFactor premiumFactor = PremiumFactor.FLAT_AMOUNT;

    @NotNull
    @JsonSerialize(using = LocalJodaDateSerializer.class)
    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    private LocalDate effectiveFrom;
    @NotNull
    private PremiumRateFrequency premiumRate = PremiumRateFrequency.MONTHLY;

    public PremiumInfluencingFactor[] getPremiumInfluencingFactors() {
        return premiumInfluencingFactors;
    }
}
