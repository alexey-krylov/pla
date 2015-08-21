package com.pla.individuallife.sharedresource.model.vo;

import com.pla.sharedkernel.identifier.PlanId;

import java.util.List;

/**
 * Created by pradyumna on 23-05-2015.
 */
public class ProposalBuilder {

    private PlanId planId;
    private ProposedAssured proposedAssured;
    private Proposer proposer;
    private ProposalPlanDetail proposalPlanDetail;
    private List<Beneficiary> beneficiaries;

    ProposalBuilder withPlanId(PlanId planId) {
        this.planId = planId;
        return this;
    }

    ProposalBuilder withProposedAssured(ProposedAssured proposedAssured) {
        this.proposedAssured = proposedAssured;
        return this;
    }

    ProposalBuilder withProposer(Proposer proposer) {
        this.proposer = proposer;
        return this;
    }

    ProposalBuilder withProposalPlanDetail(ProposalPlanDetail proposalPlanDetail) {
        this.proposalPlanDetail = proposalPlanDetail;
        return this;
    }

}
