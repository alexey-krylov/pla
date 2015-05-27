package com.pla.grouplife.quotation.domain.model.grouplife;

import com.pla.sharedkernel.identifier.CoverageId;
import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by Samir on 4/29/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "coverageCode")
public class CoveragePremiumDetail {

    private String coverageCode;

    private CoverageId coverageId;

    private BigDecimal premium;

    private String coverageName;

    private BigDecimal sumAssured;

    CoveragePremiumDetail(String coverageName, String coverageCode, CoverageId coverageId, BigDecimal premium) {
        this.coverageCode = coverageCode;
        this.coverageId = coverageId;
        this.premium = premium;
        this.coverageName = coverageName;
    }

    public CoveragePremiumDetail updateWithPremium(BigDecimal premiumAmount) {
        this.premium = premiumAmount;
        return this;
    }
}
