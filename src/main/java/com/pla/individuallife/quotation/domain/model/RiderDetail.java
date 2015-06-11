package com.pla.individuallife.quotation.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Created by Karunakar on 5/25/2015.
 */

@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(of = {"coverageId"})
public class RiderDetail {

    private String coverageId;

    private BigDecimal sumAssured;

    private Integer coverTerm;

    private Integer waiverOfPremium;

    public RiderDetail(String coverageId, BigDecimal sumAssured, Integer coverTerm, Integer waiverOfPremium) {
        this.coverageId = coverageId;
        this.sumAssured = sumAssured;
        this.coverTerm = coverTerm;
        this.waiverOfPremium = waiverOfPremium;
    }
}
