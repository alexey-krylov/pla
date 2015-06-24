package com.pla.grouphealth.quotation.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Samir on 4/29/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "coverageCode")
public class GHCoveragePremiumDetail {

    private String coverageCode;

    private CoverageId coverageId;

    private BigDecimal premium;

    private String coverageName;

    private BigDecimal sumAssured;

    private String premiumVisibility;

    private Set<BenefitPremiumLimit> benefitPremiumLimits;

    GHCoveragePremiumDetail(String coverageName, String coverageCode, CoverageId coverageId, BigDecimal premium, String premiumVisibility, BigDecimal sumAssured) {
        this.coverageCode = coverageCode;
        this.coverageId = coverageId;
        this.premium = premium;
        this.coverageName = coverageName;
        this.premiumVisibility = premiumVisibility;
        this.sumAssured = sumAssured;
    }

    public GHCoveragePremiumDetail updateWithPremium(BigDecimal premiumAmount) {
        this.premium = premiumAmount;
        return this;
    }

    public GHCoveragePremiumDetail addAllBenefitLimit(Set<BenefitPremiumLimit> benefitPremiumLimits) {
        this.benefitPremiumLimits = benefitPremiumLimits;
        return this;
    }
}
