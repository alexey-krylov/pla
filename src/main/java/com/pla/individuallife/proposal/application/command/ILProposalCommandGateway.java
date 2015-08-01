package com.pla.individuallife.proposal.application.command;


import java.util.concurrent.TimeoutException;

/**
 * Created by Prasant on 26-May-15.
 */
public interface ILProposalCommandGateway {

    String createProposal(ILCreateProposalCommand proposalCommand)
            throws TimeoutException, InterruptedException;

    String updateCompulsoryHealthStatement(ILProposalUpdateCompulsoryHealthStatementCommand updateCompulsoryHealthStatementCommand)
            throws  TimeoutException,InterruptedException;

    String updateFamilyPersonal(ILProposalUpdateFamilyPersonalDetailsCommand questionCommand)
            throws TimeoutException, InterruptedException;


    String updateWithPlanDetail(ILProposalUpdateWithPlanAndBeneficiariesCommand updateWithPlanAndBeneficiariesCommand)
            throws TimeoutException, InterruptedException;

    String updateWithProposer(ILProposalUpdateWithProposerCommand cmd)
            throws TimeoutException, InterruptedException;

    String updateGeneralDetails(ILProposalUpdateGeneralDetailsCommand cmd)
            throws TimeoutException, InterruptedException;

    String updateAdditionalDetails(ILProposalUpdateAdditionalDetailsCommand cmd)
            throws TimeoutException, InterruptedException;

    String updatePremiumPaymentDetails(ILProposalUpdatePremiumPaymentDetailsCommand cmd)
            throws TimeoutException, InterruptedException;

    String uploadMandatoryDocument(ILProposalDocumentCommand cmd)
            throws TimeoutException, InterruptedException;

    String updateProposedAssuredAndAgents(ILUpdateProposalWithProposedAssuredCommand cmd)
            throws TimeoutException, InterruptedException;

    String submitProposal(SubmitILProposalCommand cmd)
            throws TimeoutException, InterruptedException;

    String approveProposal(ILProposalApprovalCommand cmd)
            throws TimeoutException, InterruptedException;

    String returnProposal(ILProposalApprovalCommand cmd)
            throws TimeoutException, InterruptedException;

    String holdProposal(ILProposalApprovalCommand cmd)
            throws TimeoutException, InterruptedException;

    String rejectProposal(ILProposalApprovalCommand cmd)
            throws TimeoutException, InterruptedException;

    String routeToNextLevel(ILProposalUnderwriterNextLevelCommand cmd)
            throws TimeoutException, InterruptedException;
}
