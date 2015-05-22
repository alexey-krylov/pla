package com.pla.individuallife.domain.policy;

import com.google.common.base.Preconditions;
import com.pla.core.domain.model.plan.PlanDetail;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.domain.model.proposal.ProposedAssured;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import com.pla.sharedkernel.identifier.PlanId;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by pradyumna on 22-05-2015.
 */
public class ProposalSpecification {

    private PlanFinder planFinder;

    @Autowired
    public ProposalSpecification(PlanFinder planFinder) {
        this.planFinder = planFinder;
    }


    public void checkProposedAssured(ProposedAssured proposer) {
        if (proposer.getMaritalStatus() == MaritalStatus.MARRIED) {
            Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getSpouseFirstName()));
            Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getSpouseLastName()));
            Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getSpouseEmailAddress()));
        }
    }

    public void checkProposerAgainstPlan(PlanId planId, ProposedAssured proposer) {
        Map plan = planFinder.findPlanByPlanId(planId);
        PlanDetail planDetail = (PlanDetail) plan.get("planDetail");
        int minAge = planDetail.getMinEntryAge();
        int maxAge = planDetail.getMaxEntryAge();
        int proposerAge = proposer.getAgeNextBirthday();
        Preconditions.checkState(proposerAge > minAge && proposerAge < maxAge);
    }
}
