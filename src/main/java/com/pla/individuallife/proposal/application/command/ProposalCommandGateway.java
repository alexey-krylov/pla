package com.pla.individuallife.proposal.application.command;


import com.pla.individuallife.identifier.ProposalId;

import java.util.concurrent.TimeoutException;

/**
 * Created by Prasant on 26-May-15.
 */
public interface ProposalCommandGateway {

    ProposalId createProposal(CreateProposalCommand proposalCommand)
            throws TimeoutException, InterruptedException;

}
