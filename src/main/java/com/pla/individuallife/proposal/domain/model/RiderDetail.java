package com.pla.individuallife.proposal.domain.model;

import com.pla.core.domain.model.plan.SumAssured;
import com.pla.core.domain.model.plan.Term;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * Created by pradyumna on 22-05-2015.
 * <p>
 * Represents the Optional Coverage selected during Proposal.
 */
@ValueObject
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "coverageId")
public class RiderDetail {

    private CoverageId coverageId;
    private SumAssured sumAssured;
    private Term coverTerm;
    private Term waiverOfPremium;

    public RiderDetail(CoverageId coverageId, SumAssured sumAssured, Term coverTerm, Term waiverOfPremium) {
        this.coverageId = coverageId;
        this.sumAssured = sumAssured;
        this.coverTerm = coverTerm;
        this.waiverOfPremium = waiverOfPremium;
    }
}
