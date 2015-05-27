package com.pla.individuallife.proposal.application.command;

import java.util.concurrent.TimeoutException;

/**
 * Created by ASUS on 26-May-15.
 */
public interface ProposalCommandGateway {

    void createProposal(CreateProposalCommand proposalCommand)
            throws TimeoutException, InterruptedException;

}
