package com.pla.individuallife.proposal.domain.service;

import com.pla.individuallife.proposal.domain.model.ILProposalApprover;
import com.pla.individuallife.proposal.domain.model.ILProposalProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeProposalApproverRole;
import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeProposalProcessorRole;

/**
 * Created by Admin on 7/30/2015.
 */
@Component
public class ILProposalRoleAdapter {

    /*
    * Create a processor who will do the Proposer update
    * */
    public ILProposalProcessor userToProposalProcessorRole(UserDetails userDetails){
        boolean hasProposalPreprocessorRole = hasIndividualLifeProposalProcessorRole(userDetails.getAuthorities());
        if (!hasProposalPreprocessorRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Proposal processor(INDIVIDUAL_LIFE_PROPOSAL_PROCESSOR) authority");
        }
        return new ILProposalProcessor(userDetails.getUsername());

    }

    public ILProposalApprover userToProposalApproverRole(UserDetails userDetails){
        boolean hasProposalApprovalRole = hasIndividualLifeProposalApproverRole(userDetails.getAuthorities());
        if (!hasProposalApprovalRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Proposal Approval (INDIVIDUAL_LIFE_PROPOSAL_APPROVER) authority");
        }
        return new ILProposalApprover(userDetails.getUsername());
    }

}
