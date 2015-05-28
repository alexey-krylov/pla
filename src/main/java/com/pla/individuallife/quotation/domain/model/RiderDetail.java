package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.math.BigInteger;

/**
 * Created by Karunakar on 5/25/2015.
 */

@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class RiderDetail {

    @Embedded
    private CoverageId coverageId;

    private BigInteger sumAssured;

    private Integer coverTerm;

    private Integer waiverOfPremium;

    public RiderDetail(CoverageId coverageId, BigInteger sumAssured, Integer coverTerm, Integer waiverOfPremium) {
        this.coverageId = coverageId;
        this.sumAssured = sumAssured;
        this.coverTerm = coverTerm;
        this.waiverOfPremium = waiverOfPremium;
    }
}
