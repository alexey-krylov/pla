package com.pla.individuallife.proposal.domain.model;

import com.pla.individuallife.sharedresource.model.vo.ILProposerDocument;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 7/30/2015.
 */
public class ILProposalProcessor {

    private String userName;

    public ILProposalProcessor(String userName) {
        this.userName = userName;
    }

    public ILProposalAggregate updateWithPlanDetail(ILProposalAggregate aggregate,ProposalPlanDetail proposalPlanDetail, Set<Beneficiary> beneficiaries, int minAge, int maxAge) {
        return aggregate.updatePlan(proposalPlanDetail, beneficiaries,minAge,maxAge);
    }

    public ILProposalAggregate updateWithProposedAssuredAndAgentDetails(ILProposalAggregate proposalAggregate,ProposedAssured proposedAssured) {
        return proposalAggregate.updateWithProposedAssuredAndAgentDetails(proposedAssured);
    }

    public ILProposalAggregate updateWithProposer(ILProposalAggregate aggregate, Proposer proposer, AgentCommissionShareModel agentCommissionShareModel, ProposalPlanDetail planDetail) {
        return aggregate.updateWithProposer(proposer,agentCommissionShareModel,planDetail);
    }

    public ILProposalAggregate updateGeneralDetails(ILProposalAggregate aggregate, GeneralDetails generalDetails) {
        return aggregate.updateGeneralDetails(generalDetails);
    }

    public ILProposalAggregate updateAdditionalDetails(ILProposalAggregate proposalAggregate, String medicalAttendantDetails, String medicalAttendantDuration, String dateAndReason, ReplacementQuestion replacementDetails) {
       return proposalAggregate.updateAdditionalDetails(medicalAttendantDetails,medicalAttendantDuration,dateAndReason,replacementDetails);
    }

    public ILProposalAggregate updateCompulsoryHealthStatement(ILProposalAggregate proposalAggregate, List<Question> quotations) {
        return proposalAggregate.updateCompulsoryHealthStatement(quotations);
    }

    public ILProposalAggregate updateFamilyPersonalDetail(ILProposalAggregate proposalAggregate, FamilyPersonalDetail familyPersonalDetail) {
        return proposalAggregate.updateFamilyPersonalDetail(familyPersonalDetail);
    }

    public ILProposalAggregate updateWithPremiumPaymentDetail(ILProposalAggregate proposalAggregate , PremiumPaymentDetails premiumPaymentDetails) {
        return proposalAggregate.updateWithPremiumPaymentDetail(premiumPaymentDetails);
    }

    public ILProposalAggregate updateWithDocuments(ILProposalAggregate aggregate, Set<ILProposerDocument> documents) {
        return aggregate.updateWithDocuments(documents);
    }

    public ILProposalAggregate submitProposal(ILProposalAggregate proposalAggregate, String submittedBy,String comment, RoutingLevel routinglevel) {
        return proposalAggregate.submitProposal(submittedBy,DateTime.now(),comment,routinglevel);
    }

}
