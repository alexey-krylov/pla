package com.pla.individuallife.domain.service;

import com.pla.core.query.PlanFinder;
import com.pla.individuallife.domain.model.proposal.ProposalAggregate;
import com.pla.individuallife.domain.model.proposal.ProposalBuilder;
import com.pla.individuallife.domain.model.proposal.ProposalProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by pradyumna on 22-05-2015.
 */
public class ProposalService {

    private ProposalNumberGenerator proposalNumberGenerator;
    private RoleAdapter roleAdapter;
    private PlanFinder planFinder;

    @Autowired
    public ProposalService(RoleAdapter roleAdapter, ProposalNumberGenerator proposalNumberGenerator) {
        this.roleAdapter = roleAdapter;
        this.proposalNumberGenerator = proposalNumberGenerator;
    }

    public ProposalAggregate createProposal(UserDetails userDetails, ProposalBuilder proposalBuilder) {
        ProposalProcessor proposalProcessor = roleAdapter.userToProposalProcessor(userDetails);
        String quotationNumber = proposalNumberGenerator.getProposalNumber();
        return proposalProcessor.generateProposal(quotationNumber);
    }
}
