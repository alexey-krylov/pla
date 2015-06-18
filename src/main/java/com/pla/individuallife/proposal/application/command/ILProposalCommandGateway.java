package com.pla.individuallife.proposal.application.command;


import com.pla.individuallife.identifier.ProposalId;

import java.util.concurrent.TimeoutException;

/**
 * Created by Prasant on 26-May-15.
 */
public interface ILProposalCommandGateway {

    ProposalId createProposal(ILCreateProposalCommand proposalCommand)
            throws TimeoutException, InterruptedException;

    void updateCompulsoryHealthStatement(ILUpdateCompulsoryHealthStatementCommand updateCompulsoryHealthStatementCommand)
            throws  TimeoutException,InterruptedException;

    void createCompulsoryQuestion(ILCreateQuestionCommand questionCommand)
            throws TimeoutException, InterruptedException;

    ProposalId convertToProposal(ILConvertToProposalCommand convertToProposalCommand)
            throws TimeoutException, InterruptedException;

}
