package com.pla.core.application.plan.premium;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.domain.model.PremiumFactor;
import com.pla.sharedkernel.domain.model.PremiumRateFrequency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.springframework.web.multipart.MultipartFile;

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

    private String planId;

    private String coverageId;

    private PremiumInfluencingFactor[] premiumInfluencingFactors;

    private MultipartFile file;

    private PremiumFactor premiumFactor;

    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    private LocalDate effectiveFrom;

    private PremiumRateFrequency premiumRate;


}
