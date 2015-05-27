package com.pla.individuallife.proposal.domain.service;

import com.pla.core.query.PlanFinder;
import com.pla.individuallife.identifier.ProposalId;
import com.pla.individuallife.proposal.domain.model.ProposalProcessor;
import com.pla.individuallife.proposal.domain.model.ProposedAssured;
import com.pla.individuallife.proposal.domain.model.Proposer;
import com.pla.individuallife.quotation.domain.service.ILQuotationRoleAdapter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by pradyumna on 22-05-2015.
 */
public class ProposalService {

    private ProposalNumberGenerator proposalNumberGenerator;
    private ILQuotationRoleAdapter roleAdapter;
    private PlanFinder planFinder;

    @Autowired
    public ProposalService(ILQuotationRoleAdapter roleAdapter, ProposalNumberGenerator proposalNumberGenerator) {
        this.roleAdapter = roleAdapter;
        this.proposalNumberGenerator = proposalNumberGenerator;
    }

    public ProposalId createProposal(UserDetails userDetails,
                                     ProposedAssured proposedAssured,
                                     Proposer proposer) {
        ProposalProcessor proposalProcessor = roleAdapter.userToProposalProcessor(userDetails);
        ProposalId proposalId = new ProposalId(new ObjectId().toString());
        String proposalNumber = proposalNumberGenerator.getProposalNumber();
        proposalProcessor.generateProposal(proposalNumber);
        return proposalId;
    }
}
