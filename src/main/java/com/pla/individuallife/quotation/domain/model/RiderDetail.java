package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;

/**
 * Created by Karunakar on 5/25/2015.
 */

@Entity
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = "optionalRidersId")
public class RiderDetail {

    @Id
    private String riderDetailId;

    private CoverageId coverageId;

    private BigInteger sumAssured;

    private Integer coverTerm;

    private Integer waiverOfPremium;

    public RiderDetail(String riderDetailId, CoverageId coverageId, BigInteger sumAssured, Integer coverTerm, Integer waiverOfPremium) {
        this.riderDetailId = riderDetailId;
        this.coverageId = coverageId;
        this.sumAssured = sumAssured;
        this.coverTerm = coverTerm;
        this.waiverOfPremium = waiverOfPremium;
    }
}
