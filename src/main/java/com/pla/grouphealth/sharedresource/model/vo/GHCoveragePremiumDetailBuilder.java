package com.pla.grouphealth.sharedresource.model.vo;

import com.google.common.collect.Sets;
import com.pla.sharedkernel.identifier.BenefitId;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Samir on 6/9/2015.
 */
@Getter
public class GHCoveragePremiumDetailBuilder {

    private String coverageName;

    private String coverageCode;

    private String coverageId;

    private BigDecimal premium;

    private String premiumVisibility;

    private BigDecimal sumAssured;

    private Set<BenefitPremiumLimit> benefitPremiumLimits;

    public GHCoveragePremiumDetailBuilder(String coverageCode, String coverageId, String coverageName, BigDecimal premium, String premiumVisibility,BigDecimal sumAssured) {
        this.coverageCode = coverageCode;
        this.coverageId = coverageId;
        this.coverageName = coverageName;
        this.premium = premium;
        this.premiumVisibility = premiumVisibility;
        this.sumAssured=sumAssured;
    }

    public GHCoveragePremiumDetailBuilder withBenefit(String benefitCode, String benefitId, BigDecimal benefitLimit) {
        BenefitPremiumLimit benefitPremiumLimit = new BenefitPremiumLimit(benefitCode, new BenefitId(benefitId), benefitLimit);
        if (isEmpty(this.benefitPremiumLimits)) {
            this.benefitPremiumLimits = Sets.newHashSet();
        }
        this.benefitPremiumLimits.add(benefitPremiumLimit);
        return this;
    }
}
