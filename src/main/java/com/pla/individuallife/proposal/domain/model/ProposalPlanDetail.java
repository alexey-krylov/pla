package com.pla.individuallife.proposal.domain.model;

import com.pla.core.domain.model.plan.SumAssured;
import com.pla.core.domain.model.plan.Term;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
public class ProposalPlanDetail {

    private PlanId planId;
    private SumAssured sumAssured;
    private Term policyTerm;
    private Term paymentTerm;
    private Term premiumTerm;

    ProposalPlanDetail(PlanId planId, SumAssured sumAssured, Term policyTerm, Term paymentTerm, Term premiumTerm) {
        this.planId = planId;
        this.sumAssured = sumAssured;
        this.policyTerm = policyTerm;
        this.paymentTerm = paymentTerm;
        this.premiumTerm = premiumTerm;
    }


}
