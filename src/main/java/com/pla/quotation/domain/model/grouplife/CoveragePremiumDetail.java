package com.pla.quotation.domain.model.grouplife;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by Samir on 4/29/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
class CoveragePremiumDetail {

    private String coverageCode;

    private String coverageId;

    private BigDecimal premium;

    private String coverageName;

    private BigDecimal sumAssured;

    CoveragePremiumDetail(String coverageName, String coverageCode, String coverageId, BigDecimal premium) {
        this.coverageCode = coverageCode;
        this.coverageId = coverageId;
        this.premium = premium;
        this.coverageName = coverageName;
    }
}
